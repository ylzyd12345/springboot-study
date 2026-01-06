#!/usr/bin/env node

import { readFileSync, writeFileSync, existsSync, mkdirSync, rmSync, statSync } from 'fs';
import { join, dirname, basename, resolve, extname } from 'path';
import { fileURLToPath } from 'url';
import { marked } from 'marked';
import { Document, Packer, Paragraph, TextRun, ImageRun, HeadingLevel, Table, TableRow, TableCell, WidthType, AlignmentType, BorderStyle, TableLayoutType } from 'docx';
import { execSync } from 'child_process';
import sizeOf from 'image-size';
import JSZip from 'jszip';
import { parseString, Builder } from 'xml2js';
import { randomUUID } from 'crypto';

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

// 配置
const CONFIG = {
  mermaidWidth: 3000,
  mermaidScale: 3,
  tempDir: `./temp_${randomUUID()}`,  // 使用唯一ID避免并发冲突
  maxWidth: 6.5,  // 英寸，适配Word页面宽度（留出边距）
  minImageHeight: 2, // 最小图片高度（英寸）
};

// 创建临时目录
function ensureTempDir() {
  if (!existsSync(CONFIG.tempDir)) {
    mkdirSync(CONFIG.tempDir, { recursive: true });
    console.log(`✓ 创建临时目录: ${CONFIG.tempDir}`);
  }
}

// 清理临时目录
function cleanupTempDir() {
  if (existsSync(CONFIG.tempDir)) {
    rmSync(CONFIG.tempDir, { recursive: true, force: true });
    console.log(`✓ 清理临时目录: ${CONFIG.tempDir}`);
  }
}

// 提取 Mermaid 代码块
function extractMermaidBlocks(markdown) {
  const mermaidPattern = /```mermaid\n([\s\S]*?)```/g;
  const blocks = [];
  let match;
  let index = 0;

  while ((match = mermaidPattern.exec(markdown)) !== null) {
    blocks.push({
      code: match[1].trim(),
      index: index++,
      placeholder: `__MERMAID_${index}__`,
      originalText: match[0]
    });
  }

  console.log(`\n=== 步骤1: 提取 Mermaid 代码块 ===`);
  console.log(`发现 ${blocks.length} 个 Mermaid 代码块`);
  blocks.forEach((block, i) => {
    console.log(`  [${i + 1}] 占位符: ${block.placeholder}`);
    console.log(`      代码长度: ${block.code.length} 字符`);
    console.log(`      原始文本: ${block.originalText.substring(0, 50)}...`);
  });

  return blocks;
}

// 替换 Mermaid 代码块为占位符
function replaceMermaidWithPlaceholders(markdown, blocks) {
  let result = markdown;
  blocks.forEach((block, idx) => {
    result = result.replace(
      /```mermaid\n[\s\S]*?```/,
      `\n\n![Mermaid Diagram ${idx + 1}](${block.placeholder})\n\n`
    );
  });

  console.log(`\n=== 步骤2: 替换 Mermaid 代码块为占位符 ===`);
  console.log(`已替换 ${blocks.length} 个 Mermaid 代码块`);

  return result;
}

// 生成 Mermaid 图片
function generateMermaidImage(code, index) {
  console.log(`\n=== 步骤3: 生成 Mermaid 图片 ${index + 1} ===`);

  const tempMmdFile = join(CONFIG.tempDir, `mermaid_${index}.mmd`);
  const tempPngFile = join(CONFIG.tempDir, `mermaid_${index}.png`);

  // 保存 Mermaid 代码
  writeFileSync(tempMmdFile, code);
  console.log(`  ✓ 保存 Mermaid 代码: ${tempMmdFile}`);
  console.log(`    代码内容预览: ${code.substring(0, 100)}...`);

  // 生成图片
  try {
    console.log(`  执行命令: npx @mermaid-js/mermaid-cli -i "${tempMmdFile}" -o "${tempPngFile}" -w ${CONFIG.mermaidWidth} -s ${CONFIG.mermaidScale}`);
    execSync(
      `npx @mermaid-js/mermaid-cli -i "${tempMmdFile}" -o "${tempPngFile}" -w ${CONFIG.mermaidWidth} -s ${CONFIG.mermaidScale}`,
      { stdio: 'pipe' }
    );
    console.log(`  ✓ Mermaid CLI 执行成功`);
  } catch (error) {
    console.error(`  ✗ 生成 Mermaid 图片失败: ${error.message}`);
    return null;
  }

  // 检查图片是否存在
  if (!existsSync(tempPngFile)) {
    console.error(`  ✗ 图片文件不存在: ${tempPngFile}`);
    return null;
  }

  // 检查图片大小
  const stats = statSync(tempPngFile);
  console.log(`  ✓ 图片文件存在: ${tempPngFile}`);
  console.log(`    文件大小: ${(stats.size / 1024).toFixed(2)} KB`);

  // 检查图片尺寸
  try {
    const dimensions = sizeOf(tempPngFile);
    console.log(`    图片尺寸: ${dimensions.width} x ${dimensions.height} 像素`);

    // 检查清晰度是否达标
    const isHighQuality = dimensions.width >= 2000 && dimensions.height >= 2000;
    console.log(`    清晰度检查: ${isHighQuality ? '✓ 达标' : '✗ 未达标'}`);

    if (!isHighQuality) {
      console.warn(`    警告: 图片清晰度可能不足，建议增加 mermaidWidth 或 mermaidScale`);
    }
  } catch (error) {
    console.error(`  ✗ 获取图片尺寸失败: ${error.message}`);
  }

  return tempPngFile;
}

// 获取图片尺寸
function getImageDimensions(imagePath) {
  try {
    const dimensions = sizeOf(imagePath);
    const aspectRatio = dimensions.width / dimensions.height;

    let width = CONFIG.maxWidth;
    let height = width / aspectRatio;

    // 确保图片不会太小
    if (height < CONFIG.minImageHeight) {
      height = CONFIG.minImageHeight;
      width = height * aspectRatio;
      if (width > CONFIG.maxWidth) {
        width = CONFIG.maxWidth;
        height = width / aspectRatio;
      }
    }

    return { width, height };
  } catch (error) {
    console.error(`获取图片尺寸失败: ${error.message}`);
    return { width: CONFIG.maxWidth, height: CONFIG.minImageHeight };
  }
}

// 读取图片文件为 Buffer
function readImageBuffer(imagePath) {
  try {
    return readFileSync(imagePath);
  } catch (error) {
    console.error(`读取图片失败: ${error.message}`);
    return null;
  }
}

// 解析 Markdown 并转换为 Word 元素
async function parseMarkdownToWord(markdown, baseDir, mermaidImages = {}) {
  console.log(`\n=== 步骤4: 解析 Markdown 并转换为 Word 元素 ===`);

  const tokens = marked.lexer(markdown);
  console.log(`解析到 ${tokens.length} 个 token`);

  const elements = [];
  let elementCount = 0;
  let imageCount = 0;
  let mermaidImageCount = 0;

  for (const token of tokens) {
    elementCount++;

    switch (token.type) {
      case 'heading':
        elements.push(
          new Paragraph({
            text: token.text,
            heading: mapHeadingLevel(token.depth),
            spacing: { before: 200, after: 100 }
          })
        );
        break;

      case 'paragraph':
        const paragraphElements = parseInlineTokens(token.tokens, baseDir, mermaidImages);
        elements.push(...paragraphElements);
        break;

      case 'list':
        elements.push(...parseList(token, baseDir, mermaidImages));
        break;

      case 'code':
        elements.push(
          new Paragraph({
            children: [
              new TextRun({
                text: token.text,
                font: 'Consolas',
                size: 20
              })
            ],
            spacing: { before: 100, after: 100 }
          })
        );
        break;

      case 'blockquote':
        elements.push(
          new Paragraph({
            children: [
              new TextRun({
                text: token.text,
                italics: true,
                color: '666666'
              })
            ],
            indent: { left: 720 },
            spacing: { before: 100, after: 100 }
          })
        );
        break;

      case 'table':
        elements.push(parseTable(token));
        break;

      case 'hr':
        elements.push(
          new Paragraph({
            children: [new TextRun({ text: '' })],
            border: {
              bottom: { style: BorderStyle.SINGLE, size: 1, color: 'CCCCCC' }
            },
            spacing: { before: 200, after: 200 }
          })
        );
        break;
    }
  }

  console.log(`✓ 生成 ${elements.length} 个 Word 元素`);
  console.log(`  - 包含 ${imageCount} 个图片`);
  console.log(`  - 其中 ${mermaidImageCount} 个 Mermaid 图片`);

  return elements;
}

// 映射标题级别
function mapHeadingLevel(depth) {
  const levels = [
    HeadingLevel.HEADING_1,
    HeadingLevel.HEADING_2,
    HeadingLevel.HEADING_3,
    HeadingLevel.HEADING_4,
    HeadingLevel.HEADING_5,
    HeadingLevel.HEADING_6
  ];
  return levels[depth - 1] || HeadingLevel.HEADING_1;
}

// 解析内联元素
function parseInlineTokens(tokens, baseDir, mermaidImages = {}) {
  const elements = [];
  const runs = [];
  let hasImage = false;

  for (const token of tokens) {
    switch (token.type) {
      case 'text':
        runs.push(new TextRun({ text: token.text }));
        break;

      case 'strong':
        runs.push(new TextRun({ text: token.text, bold: true }));
        break;

      case 'em':
        runs.push(new TextRun({ text: token.text, italics: true }));
        break;

      case 'codespan':
        runs.push(new TextRun({ text: token.text, font: 'Consolas', size: 20 }));
        break;

      case 'link':
        runs.push(new TextRun({ text: token.text, underline: {} }));
        break;

      case 'image':
        console.log(`\n  发现图片 token: ${token.href}`);

        // 如果之前有文本内容，先创建段落
        if (runs.length > 0) {
          elements.push(
            new Paragraph({
              children: runs,
              spacing: { before: 100, after: 100 }
            })
          );
          runs.length = 0; // 清空 runs
        }

        // 检查是否是 Mermaid 图片
        const isMermaidImage = token.href.includes('__MERMAID_');
        console.log(`    是否为 Mermaid 图片: ${isMermaidImage}`);

        let imagePath;
        let buffer;

        if (isMermaidImage) {
          console.log(`    Mermaid 占位符: ${token.href}`);

          // 检查 mermaidImages 是否存在且包含该占位符
          if (mermaidImages && mermaidImages[token.href]) {
            imagePath = mermaidImages[token.href];
            console.log(`    ✓ 找到 Mermaid 图片: ${imagePath}`);
          } else {
            console.log(`    ✗ 未找到 Mermaid 图片，跳过此图片`);
            continue;
          }
        } else {
          // 普通图片使用相对路径
          imagePath = resolve(baseDir, token.href);
        }

        console.log(`    最终图片路径: ${imagePath}`);

        if (existsSync(imagePath)) {
          buffer = readImageBuffer(imagePath);
          if (buffer) {
            // 获取原始图片尺寸
            const originalDimensions = getImageDimensions(imagePath);
            console.log(`    原始图片尺寸: ${originalDimensions.width.toFixed(2)}" x ${originalDimensions.height.toFixed(2)}"`);

            // 计算显示尺寸（保持宽高比）
            let displayWidth = originalDimensions.width;
            let displayHeight = originalDimensions.height;

            // 如果图片宽度超过最大宽度，按比例缩放
            if (displayWidth > CONFIG.maxWidth) {
              const ratio = CONFIG.maxWidth / displayWidth;
              displayWidth = CONFIG.maxWidth;
              displayHeight = displayHeight * ratio;
            }

            console.log(`    显示尺寸: ${displayWidth.toFixed(2)}" x ${displayHeight.toFixed(2)}"`);

            // 图片独占一个段落，居中对齐
            elements.push(
              new Paragraph({
                children: [
                  new ImageRun({
                    data: buffer,
                    transformation: {
                      width: displayWidth,
                      height: displayHeight
                    }
                  })
                ],
                alignment: AlignmentType.CENTER,
                spacing: { before: 200, after: 200 }
              })
            );
            console.log(`    ✓ 图片已添加到 Word（独占一行，保持原始分辨率）`);
            hasImage = true;
          }
        } else {
          console.log(`    ✗ 图片文件不存在: ${imagePath}`);
        }
        break;
    }
  }

  // 如果还有剩余的文本内容，创建段落
  if (runs.length > 0) {
    elements.push(
      new Paragraph({
        children: runs,
        spacing: { before: 100, after: 100 }
      })
    );
  }

  return elements;
}

// 解析列表
function parseList(token, baseDir, mermaidImages = {}) {
  const elements = [];
  let counter = 1; // 用于有序列表的计数器

  for (const item of token.items) {
    const runs = [];

    for (const subToken of item.tokens) {
      if (subToken.type === 'text') {
        runs.push(new TextRun({ text: subToken.text }));
      } else if (subToken.type === 'list') {
        // 嵌套列表
        elements.push(...parseList(subToken, baseDir, mermaidImages));
      }
    }

    if (runs.length > 0) {
      elements.push(
        new Paragraph({
          children: [
            new TextRun({
              text: token.ordered ? `${counter}. ` : '• ',
              bold: true
            }),
            ...runs
          ],
          indent: { left: token.ordered ? 720 : 360 },
          spacing: { before: 50, after: 50 }
        })
      );
      counter++; // 递增计数器
    }
  }

  return elements;
}

// 解析表格
function parseTable(token) {
  const rows = [];
  const columnCount = token.header.length;
  // 使用百分比宽度（fiftieths of a percent 单位）
  // Word 中 pct 类型使用 fiftieths of a percent，所以 100% = 5000
  const columnWidth = Math.round(5000 / columnCount);

  // 表头
  const headerCells = token.header.map(cell => {
    return new TableCell({
      children: [new Paragraph({ text: cell.text, bold: true })],
      width: { size: columnWidth, type: WidthType.PERCENTAGE },
      shading: { fill: 'F0F0F0' }
    });
  });
  rows.push(new TableRow({ children: headerCells }));

  // 表格内容
  for (const row of token.rows) {
    const cells = row.map(cell => {
      return new TableCell({
        children: [new Paragraph({ text: cell.text })],
        width: { size: columnWidth, type: WidthType.PERCENTAGE }
      });
    });
    rows.push(new TableRow({ children: cells }));
  }

  return new Table({
    rows: rows,
    width: { size: 9026, type: WidthType.DXA },
    margins: { top: 100, bottom: 100, left: 100, right: 100 },
    // 使用 autofit 布局模式
    layout: TableLayoutType.AUTOFIT
  });
}

// 调整 Word 文档中所有图片的宽度
async function adjustImageWidths(docxPath, targetWidthInches) {
  console.log(`  开始调整图片宽度...`);

  try {
    // 读取 DOCX 文件
    const zip = await JSZip.loadAsync(readFileSync(docxPath));

    // 获取 document.xml 文件
    const documentXml = await zip.file('word/document.xml').async('string');

    // Word 中的英寸转 EMU 单位 (1 inch = 914400 EMU)
    const targetWidthEMU = Math.round(targetWidthInches * 914400);

    let imageCount = 0;

    // 使用正则表达式查找并替换图片宽度
    // 格式: <wp:extent cx="原始宽度" cy="原始高度"/>
    const extentPattern = /<wp:extent\s+cx="(\d+)"\s+cy="(\d+)"/g;

    let newXml = documentXml;
    let match;

    while ((match = extentPattern.exec(documentXml)) !== null) {
      const originalCx = parseInt(match[1]);
      const originalCy = parseInt(match[2]);

      // 计算新的高度（保持宽高比）
      const scale = targetWidthEMU / originalCx;
      const newCy = Math.round(originalCy * scale);

      // 替换为新的宽度
      const oldExtent = match[0];
      const newExtent = `<wp:extent cx="${targetWidthEMU}" cy="${newCy}"`;

      newXml = newXml.replace(oldExtent, newExtent);

      imageCount++;
      console.log(`    图片 ${imageCount}: ${originalCx} -> ${targetWidthEMU} EMU (${(originalCx / 914400).toFixed(2)}" -> ${targetWidthInches}")`);
    }

    // 更新 DOCX 文件
    zip.file('word/document.xml', newXml);

    // 保存文件
    const outputBuffer = await zip.generateAsync({ type: 'nodebuffer', compression: 'DEFLATE' });
    writeFileSync(docxPath, outputBuffer);

    console.log(`  ✓ 已调整 ${imageCount} 张图片的宽度`);
    console.log(`  ✓ 所有图片宽度已设置为 ${targetWidthInches} 英寸`);

  } catch (error) {
    console.error(`  ✗ 调整图片宽度失败: ${error.message}`);
    console.error(`  错误堆栈: ${error.stack}`);
  }
}

// 修复单元格宽度格式（移除百分号，使用 fiftieths of a percent 单位）
async function adjustTableCellWidths(docxPath) {
  console.log(`  开始修复单元格宽度格式...`);

  try {
    // 读取 DOCX 文件
    const zip = await JSZip.loadAsync(readFileSync(docxPath));

    // 获取 document.xml 文件
    const documentXml = await zip.file('word/document.xml').async('string');

    // 修复单元格宽度格式
    // 将 <w:tcW w:type="pct" w:w="1667%"/> 改为 <w:tcW w:type="pct" w:w="1667"/>
    // 或者 <w:tcW w:w="1667%" w:type="pct"/> 改为 <w:tcW w:w="1667" w:type="pct"/>
    const pattern = /<w:tcW[^>]*w:w="(\d+)%"[^>]*\/>/g;
    const matches = documentXml.match(pattern);
    console.log(`    找到 ${matches ? matches.length : 0} 个需要修复的单元格宽度`);

    const newXml = documentXml.replace(
      pattern,
      (match, p1) => {
        // 移除百分号，保持其他属性不变
        return match.replace(`w:w="${p1}%"`, `w:w="${p1}"`);
      }
    );

    // 更新 DOCX 文件
    zip.file('word/document.xml', newXml);

    // 保存文件
    const outputBuffer = await zip.generateAsync({ type: 'nodebuffer', compression: 'DEFLATE' });
    writeFileSync(docxPath, outputBuffer);

    console.log(`  ✓ 单元格宽度格式已修复`);

  } catch (error) {
    console.error(`  ✗ 修复单元格宽度格式失败: ${error.message}`);
    console.error(`  错误堆栈: ${error.stack}`);
  }
}

// 设置表格网格宽度（修复表格显示过窄的问题）
async function adjustTableGrid(docxPath) {
  console.log(`  开始设置表格网格宽度...`);

  try {
    // 读取 DOCX 文件
    const zip = await JSZip.loadAsync(readFileSync(docxPath));

    // 获取 document.xml 文件
    const documentXml = await zip.file('word/document.xml').async('string');

    // 查找所有表格并设置正确的网格宽度
    const newXml = documentXml.replace(
      /<w:tbl\b([^>]*)>([\s\S]*?)<\/w:tbl>/g,
      (match, attrs, content) => {
        // 检查是否已经有 tblGrid
        if (content.includes('<w:tblGrid')) {
          // 如果有 tblGrid，更新它
          return match.replace(
            /<w:tblGrid[^>]*>.*?<\/w:tblGrid>/,
            (gridMatch) => {
              // 提取现有的 gridCol 数量
              const gridCols = gridMatch.match(/<w:gridCol[^>]*>/g) || [];
              const colCount = gridCols.length;
              
              if (colCount > 0) {
                // 计算每列的宽度（平均分配 9026 twip）
                const baseWidth = Math.floor(9026 / colCount);
                const remainder = 9026 - (baseWidth * (colCount - 1));
                
                // 生成新的 gridCol
                let newGridCols = '';
                for (let i = 0; i < colCount; i++) {
                  const isLast = (i === colCount - 1);
                  const width = isLast ? remainder : baseWidth;
                  newGridCols += `<w:gridCol w:w="${width}"/>`;
                }
                
                return `<w:tblGrid>${newGridCols}</w:tblGrid>`;
              }
              return gridMatch;
            }
          );
        } else {
          // 如果没有 tblGrid，添加它
          // 先找到列数
          const tcWMatches = content.match(/<w:tcW[^>]*>/g);
          const colCount = tcWMatches ? tcWMatches.length : 0;
          
          if (colCount > 0) {
            // 计算每列的宽度
            const baseWidth = Math.floor(9026 / colCount);
            const remainder = 9026 - (baseWidth * (colCount - 1));
            
            // 生成新的 gridCol
            let newGridCols = '';
            for (let i = 0; i < colCount; i++) {
              const isLast = (i === colCount - 1);
              const width = isLast ? remainder : baseWidth;
              newGridCols += `<w:gridCol w:w="${width}"/>`;
            }
            
            // 在 tblPr 后面插入 tblGrid
            return match.replace(
              /(<\/w:tblPr>)/,
              `$1<w:tblGrid>${newGridCols}</w:tblGrid>`
            );
          }
          return match;
        }
      }
    );

    // 更新 DOCX 文件
    zip.file('word/document.xml', newXml);

    // 保存文件
    const outputBuffer = await zip.generateAsync({ type: 'nodebuffer', compression: 'DEFLATE' });
    writeFileSync(docxPath, outputBuffer);

    console.log(`  ✓ 表格网格宽度已设置`);

  } catch (error) {
    console.error(`  ✗ 设置表格网格宽度失败: ${error.message}`);
    console.error(`  错误堆栈: ${error.stack}`);
  }
}

// Promise 包装的 parseString
function parseStringPromise(xml) {
  return new Promise((resolve, reject) => {
    parseString(xml, { explicitArray: false, mergeAttrs: true }, (err, result) => {
      if (err) reject(err);
      else resolve(result);
    });
  });
}

// 检查 pandoc 是否已安装
function checkPandocInstalled() {
  try {
    execSync('which pandoc', { stdio: 'pipe' });
    return true;
  } catch (error) {
    return false;
  }
}

// 安装 pandoc（通过 Homebrew）
function installPandoc() {
  console.log(`=== 步骤1: 安装 Pandoc ===`);
  try {
    console.log('检测到系统未安装 pandoc，尝试通过 Homebrew 安装...');
    execSync('brew install pandoc', { stdio: 'inherit' });
    console.log('✓ Pandoc 安装成功');
    return true;
  } catch (error) {
    console.error('✗ Pandoc 安装失败，请手动安装 pandoc');
    console.error('  macOS: brew install pandoc');
    console.error('  或访问: https://pandoc.org/installing.html');
    return false;
  }
}

// 使用 pandoc 转换 Markdown 为 Word
function convertWithPandoc(inputPath, outputPath) {
  console.log(`\n=== 步骤2: 使用 Pandoc 转换为 Word ===`);
  try {
    // 获取输入文件的目录和文件名
    const inputDir = dirname(inputPath);
    const inputFileName = basename(inputPath);
    const outputFileName = basename(outputPath);
    
    // 使用 cwd 选项指定工作目录，避免改变当前进程的工作目录
    const cmd = `pandoc "${inputFileName}" -o "${outputFileName}"`;
    console.log(`执行命令: ${cmd}`);
    console.log(`工作目录: ${inputDir}`);
    execSync(cmd, { stdio: 'pipe', cwd: inputDir });
    console.log(`✓ Pandoc 转换成功: ${outputPath}`);
    
    const stats = statSync(outputPath);
    console.log(`  文件大小: ${(stats.size / 1024 / 1024).toFixed(2)} MB`);
    return true;
  } catch (error) {
    console.error(`✗ Pandoc 转换失败: ${error.message}`);
    return false;
  }
}

// 替换 Word 文档中的 Mermaid 代码块为图片
async function replaceMermaidInWord(docxPath, mermaidImages) {
  console.log(`\n=== 步骤4: 替换 Word 中的 Mermaid 代码块为图片 ===`);

  try {
    // 读取 DOCX 文件
    const zip = await JSZip.loadAsync(readFileSync(docxPath));

    // 获取 document.xml 文件
    let documentXml = await zip.file('word/document.xml').async('string');

    // 查找所有 SourceCode 样式的段落（pandoc 将代码块转换为这种样式）
    // 查找包含 "flowchart TD" 或 "erDiagram" 或 "sequenceDiagram" 的段落
    const codePatterns = [
      /<w:p><w:pPr><w:pStyle w:val="SourceCode" \/>([\s\S]*?)<\/w:p>/g,
      /<w:p><w:pPr><w:pStyle w:val="SourceCode" \/><w:numPr>[\s\S]*?<\/w:numPr><\/w:pPr>([\s\S]*?)<\/w:p>/g
    ];

    let replacementCount = 0;

    // 遍历所有 Mermaid 图片
    for (const [placeholder, imagePath] of Object.entries(mermaidImages)) {
      // 读取图片文件
      const imageBuffer = readFileSync(imagePath);
      const dimensions = sizeOf(imagePath);

      // 生成图片关系 ID
      const relId = `rId${1000 + replacementCount}`;

      // 添加图片到 media 文件夹
      const imageName = `mermaid_${replacementCount}.png`;
      zip.file(`word/media/${imageName}`, imageBuffer);

      // 添加图片关系到 document.xml.rels
      let relsXml = await zip.file('word/_rels/document.xml.rels').async('string');
      const relEntry = `<Relationship Id="${relId}" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/image" Target="media/${imageName}"/>`;
      relsXml = relsXml.replace('</Relationships>', `${relEntry}</Relationships>`);
      zip.file('word/_rels/document.xml.rels', relsXml);

      // 构建图片的 XML 结构
      // Word 中的英寸转 EMU 单位 (1 inch = 914400 EMU)
      const widthEMU = Math.round((dimensions.width / 96) * 914400); // 假设 96 DPI
      const heightEMU = Math.round((dimensions.height / 96) * 914400);

      // 限制最大宽度为 6.5 英寸
      const maxWidthEMU = Math.round(6.5 * 914400);
      let finalWidthEMU = widthEMU;
      let finalHeightEMU = heightEMU;

      if (widthEMU > maxWidthEMU) {
        const ratio = maxWidthEMU / widthEMU;
        finalWidthEMU = maxWidthEMU;
        finalHeightEMU = Math.round(heightEMU * ratio);
      }

      const imageXml = `<w:p><w:pPr><w:jc w:val="center"/></w:pPr><w:r><w:rPr/><w:drawing><wp:inline distT="0" distB="0" distL="0" distR="0"><wp:extent cx="${finalWidthEMU}" cy="${finalHeightEMU}"/><wp:effectExtent l="0" t="0" r="0" b="0"/><wp:docPr id="${1 + replacementCount}" name="Picture ${1 + replacementCount}"/><wp:cNvGraphicFramePr><a:graphicFrameLocks xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" noChangeAspect="1"/></wp:cNvGraphicFramePr><a:graphic xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main"><a:graphicData uri="http://schemas.openxmlformats.org/drawingml/2006/picture"><pic:pic xmlns:pic="http://schemas.openxmlformats.org/drawingml/2006/picture"><pic:nvPicPr><pic:cNvPr id="${1 + replacementCount}" name="Picture ${1 + replacementCount}"/><pic:cNvPicPr/></pic:nvPicPr><pic:blipFill><a:blip r:embed="${relId}" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships"/><a:stretch><a:fillRect/></a:stretch></pic:blipFill><pic:spPr><a:xfrm><a:off x="0" y="0"/><a:ext cx="${finalWidthEMU}" cy="${finalHeightEMU}"/></a:xfrm><a:prstGeom prst="rect"><a:avLst/></a:prstGeom></pic:spPr></pic:pic></a:graphicData></a:graphic></wp:inline></w:drawing></w:r></w:p>`;

      // 查找并替换 SourceCode 样式的段落
      let found = false;
      for (const pattern of codePatterns) {
        const matches = documentXml.match(pattern);
        if (matches) {
          for (const match of matches) {
            // 检查是否包含 Mermaid 关键词（支持所有类型）
            const mermaidKeywords = [
              'flowchart', 'graph', 'erDiagram', 'sequenceDiagram', 
              'gantt', 'classDiagram', 'stateDiagram', 'pie', 'journey',
              'mindmap', 'timeline', 'gitGraph', 'C4Context', 'architecture',
              'requirementDiagram', 'block', 'network', 'sankey', 'xychart',
              'block-beta', 'info', 'flowchart LR', 'flowchart TB', 
              'flowchart BT', 'flowchart RL', 'TD', 'LR', 'TB', 'BT', 'RL'
            ];
            
            const hasMermaid = mermaidKeywords.some(keyword => match.includes(keyword));
            
            if (hasMermaid) {
              documentXml = documentXml.replace(match, imageXml);
              found = true;
              console.log(`  ✓ 替换 Mermaid 代码块 ${replacementCount + 1} 为图片`);
              replacementCount++;
              break;
            }
          }
          if (found) break;
        }
      }

      if (!found) {
        console.log(`  ✗ 未找到 Mermaid 代码块 ${replacementCount + 1}`);
      }
    }

    // 更新 document.xml
    zip.file('word/document.xml', documentXml);

    // 保存文件
    const outputBuffer = await zip.generateAsync({ type: 'nodebuffer', compression: 'DEFLATE' });
    writeFileSync(docxPath, outputBuffer);

    console.log(`  ✓ 已替换 ${replacementCount} 个 Mermaid 代码块`);

  } catch (error) {
    console.error(`  ✗ 替换 Mermaid 代码块失败: ${error.message}`);
    console.error(`  错误堆栈: ${error.stack}`);
  }
}

// 主函数
async function main() {
  const args = process.argv.slice(2);
  if (args.length < 1) {
    console.log('用法: node index.js <markdown文件> [输出文件]');
    console.log('示例: node index.js 产品需求文档.md 产品需求文档.docx');
    process.exit(1);
  }

  const inputPath = args[0];
  const outputPath = args[1] || inputPath.replace(/\.md$/, '.docx');

  if (!existsSync(inputPath)) {
    console.error(`错误: 文件不存在 ${inputPath}`);
    process.exit(1);
  }

  console.log(`\n========================================`);
  console.log(`Markdown to Word Converter`);
  console.log(`========================================`);
  console.log(`输入文件: ${inputPath}`);
  console.log(`输出文件: ${outputPath}`);
  console.log(`========================================\n`);

  try {
    ensureTempDir();

    // 步骤1: 检查并安装 pandoc
    if (!checkPandocInstalled()) {
      if (!installPandoc()) {
        process.exit(1);
      }
    } else {
      console.log('✓ 检测到 Pandoc 已安装');
    }

    // 步骤2: 使用 pandoc 转换为 Word
    if (!convertWithPandoc(inputPath, outputPath)) {
      process.exit(1);
    }

    // 读取 Markdown 文件以提取 Mermaid 代码块
    const markdown = readFileSync(inputPath, 'utf-8');
    console.log(`✓ 读取 Markdown 文件: ${markdown.length} 字符`);

    // 步骤3: 提取并生成 Mermaid 图片
    console.log(`\n=== 步骤3: 提取并生成 Mermaid 图片 ===`);
    const mermaidBlocks = extractMermaidBlocks(markdown);
    const mermaidImages = {};

    if (mermaidBlocks.length > 0) {
      console.log(`发现 ${mermaidBlocks.length} 个 Mermaid 代码块，开始生成图片...`);
      for (const block of mermaidBlocks) {
        const imagePath = generateMermaidImage(block.code, block.index);
        if (imagePath) {
          mermaidImages[block.placeholder] = imagePath;
          console.log(`  ✓ Mermaid 图片 ${block.index + 1} 生成成功`);
        } else {
          console.log(`  ✗ Mermaid 图片 ${block.index + 1} 生成失败`);
        }
      }

      console.log(`\nMermaid 图片映射表:`);
      Object.entries(mermaidImages).forEach(([placeholder, path]) => {
        console.log(`  ${placeholder} -> ${path}`);
      });

      // 步骤4: 替换 Word 中的 Mermaid 代码块为图片
      await replaceMermaidInWord(outputPath, mermaidImages);
    } else {
      console.log(`未发现 Mermaid 代码块，跳过图片生成步骤`);
    }

    // 步骤5: 后处理 - 调整图片宽度（已注释）
    // console.log(`\n=== 步骤5: 后处理 - 调整图片宽度 ===`);
    // await adjustImageWidths(outputPath, CONFIG.maxWidth);

    // 步骤6: 后处理 - 修复单元格宽度格式（已注释）
    // console.log(`\n=== 步骤6: 后处理 - 修复单元格宽度格式 ===`);
    // await adjustTableCellWidths(outputPath);

    // 步骤7: 后处理 - 设置表格网格宽度（已注释）
    // console.log(`\n=== 步骤7: 后处理 - 设置表格网格宽度 ===`);
    // await adjustTableGrid(outputPath);

    console.log(`\n========================================`);
    console.log(`转换完成！`);
    console.log(`========================================\n`);

  } catch (error) {
    console.error(`\n========================================`);
    console.error(`转换失败！`);
    console.error(`========================================`);
    console.error(`错误信息: ${error.message}`);
    console.error(`错误堆栈:\n${error.stack}`);
    process.exit(1);
  } finally {
    cleanupTempDir();
  }
}

main();
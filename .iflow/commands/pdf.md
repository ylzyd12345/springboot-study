---
name: pdf
description: 专业的PDF文档处理工具，支持文本提取、表格提取、文档创建、合并分割和表单填写
license: Proprietary. LICENSE.txt has complete terms
workflow_trigger: /pdf
agent_path: .iflow/agents/pdf
---

# PDF Document Processing Workflow

## Skill Capabilities

This skill provides comprehensive PDF manipulation toolkit with support for:

- **Text Extraction**: Extract text from PDFs with layout preservation
- **Table Extraction**: Extract tables from PDFs and convert to structured formats (Excel, CSV)
- **Document Creation**: Create new PDF documents programmatically with Python
- **Document Merging**: Combine multiple PDF files into a single document
- **Document Splitting**: Split PDFs into separate pages or page ranges
- **Form Filling**: Fill out PDF forms programmatically
- **OCR Processing**: Extract text from scanned PDFs using OCR
- **Watermarking**: Add watermarks to PDF documents
- **Password Protection**: Encrypt/decrypt PDF documents
- **Metadata Management**: Read and modify PDF metadata

**Use Cases:**
1. Extracting text and tables from PDF reports
2. Creating new PDF documents from data
3. Merging multiple PDFs into consolidated documents
4. Splitting large PDFs into smaller files
5. Filling out PDF forms programmatically
6. Processing scanned documents with OCR
7. Adding security and watermarks to PDFs

---

# Workflow Trigger Mechanism

## Command Activation

When the user invokes the `/pdf` command, the system **MUST** execute the following automated workflow:

### 1. Environment Setup

**Set working directory and Python path:**
```bash
cd .iflow/agents/pdf
export PYTHONPATH=$(pwd)
```

**All script paths are relative to this agent directory:**
- Python scripts for PDF processing
- Reference documents: `reference.md`, `forms.md`

### 2. Workflow Decision Logic

**Immediately determine task type and execute appropriate workflow:**

| User Request | Workflow Action | Best Tool |
|-------------|-----------------|-----------|
| Extract text | Use pdfplumber or pdftotext | `pdfplumber`, `pdftotext` |
| Extract tables | Use pdfplumber with pandas | `pdfplumber` |
| Create new PDF | Use reportlab | `reportlab` |
| Merge/split PDFs | Use pypdf or qpdf | `pypdf`, `qpdf` |
| Fill forms | Read forms.md | `pypdf`, `pdf-lib` |
| OCR scanned PDFs | Use pytesseract + pdf2image | `pytesseract` |
| Add watermark | Use pypdf | `pypdf` |
| Encrypt/decrypt | Use pypdf or qpdf | `pypdf`, `qpdf` |

### 3. Automatic Dependency Management

**Dependency detection and installation strategy:**

The skill follows a "lazy installation" approach:
1. **Assume dependencies are installed** - Execute directly without pre-checks
2. **Install on failure** - Only install if command fails with "command not found" or import error
3. **One-time setup** - Once installed, dependencies persist across sessions

**Installation commands by platform:**

```bash
# macOS (using Homebrew)
brew install poppler tesseract qpdf

# Install Tesseract Chinese language pack
brew install tesseract-lang  # Includes Chinese support

# Python packages
pip install pypdf pdfplumber reportlab pytesseract pdf2image pandas

# For form filling (JavaScript)
npm install -g pdf-lib
```

**Linux (apt-based distributions):**
```bash
sudo apt-get install poppler-utils tesseract-ocr tesseract-ocr-chi-sim tesseract-ocr-chi-tra qpdf

# Python packages
pip install pypdf pdfplumber reportlab pytesseract pdf2image pandas

# For form filling (JavaScript)
npm install -g pdf-lib
```

**Chinese Character Support:**
- **OCR**: Tesseract Chinese language packs (`chi_sim` for Simplified, `chi_tra` for Traditional)
- **PDF Creation**: ReportLab requires Chinese fonts (use system fonts or download open-source fonts like Noto Sans CJK)
- **Text Encoding**: All scripts use UTF-8 encoding by default to handle Chinese characters correctly

### 4. Execution Flow

**Standard execution pattern:**

```
User: /pdf [request]
  ↓
System: Parse request type
  ↓
System: cd to agent_path + set PYTHONPATH
  ↓
System: Determine appropriate tool (pypdf/pdfplumber/reportlab)
  ↓
System: Execute workflow steps
  ↓
System: Validate output (if applicable)
  ↓
System: Return result to user
```

### 5. Critical Execution Rules

1. **Choose the right tool** - Use pdfplumber for text/tables, pypdf for manipulation, reportlab for creation
2. **Execute from agent directory** - All relative paths assume agent_path as working directory
3. **Output to workspace root** - Generated PDF files MUST be saved to the workspace root (e.g., `~/Desktop/workspace/test_pdf/`) by default, NOT to the agent_path directory. Only save to agent_path if explicitly requested by user or for reference documents
4. **Install missing dependencies** - Don't fail due to missing tools, install them automatically
5. **Handle large files carefully** - Process large PDFs page by page to avoid memory issues
6. **Validate outputs** - Check extracted data quality and document integrity

---

# PDF Processing Guide

## Overview

This guide covers essential PDF processing operations using Python libraries and command-line tools. For advanced features, JavaScript libraries, and detailed examples, see reference.md. If you need to fill out a PDF form, read forms.md and follow its instructions.

## Quick Start

```python
from pypdf import PdfReader, PdfWriter

# Read a PDF
reader = PdfReader("document.pdf")
print(f"Pages: {len(reader.pages)}")

# Extract text (supports UTF-8 and Chinese characters)
text = ""
for page in reader.pages:
    text += page.extract_text()
```

## Chinese Character Support

All PDF operations fully support Chinese characters (Simplified and Traditional):

### Creating PDFs with Chinese Text

ReportLab 需要明确注册中文字体才能正确显示中文。以下是完整的示例：

```python
from reportlab.lib.pagesizes import A4
from reportlab.pdfgen import canvas
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
import platform
import os

def register_chinese_font():
    """注册中文字体（跨平台）"""
    system = platform.system()
    
    # macOS 字体路径
    if system == 'Darwin':
        font_paths = [
            '/System/Library/Fonts/PingFang.ttc',
            '/System/Library/Fonts/STHeiti Light.ttc',
            '/Library/Fonts/Arial Unicode.ttf',
        ]
    # Linux 字体路径
    elif system == 'Linux':
        font_paths = [
            '/usr/share/fonts/truetype/droid/DroidSansFallbackFull.ttf',
            '/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc',
            '/usr/share/fonts/truetype/arphic/uming.ttc',
        ]
    # Windows 字体路径
    else:
        font_paths = [
            'C:/Windows/Fonts/msyh.ttc',  # 微软雅黑
            'C:/Windows/Fonts/simsun.ttc',  # 宋体
            'C:/Windows/Fonts/simhei.ttf',  # 黑体
        ]
    
    # 尝试注册字体
    for font_path in font_paths:
        if os.path.exists(font_path):
            try:
                pdfmetrics.registerFont(TTFont('ChineseFont', font_path))
                print(f"成功注册中文字体: {font_path}")
                return True
            except Exception as e:
                continue
    
    raise RuntimeError("未找到可用的中文字体")

# 注册中文字体
register_chinese_font()

# 创建包含中文的 PDF
c = canvas.Canvas("chinese_doc.pdf", pagesize=A4)
width, height = A4

# 使用注册的中文字体
c.setFont('ChineseFont', 16)
c.drawString(100, height - 100, "这是一个包含中文的测试文档")
c.setFont('ChineseFont', 12)
c.drawString(100, height - 130, "支持简体中文和繁体中文")

c.save()
```

**使用 Platypus（高级布局）创建中文 PDF：**

```python
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib.pagesizes import A4
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
import platform
import os

# 注册中文字体（使用上面的 register_chinese_font 函数）
register_chinese_font()

# 创建文档
doc = SimpleDocTemplate("chinese_report.pdf", pagesize=A4)
story = []

# 创建使用中文字体的样式
styles = getSampleStyleSheet()
chinese_style = ParagraphStyle(
    'ChineseStyle',
    parent=styles['Normal'],
    fontName='ChineseFont',  # 关键：指定中文字体
    fontSize=12,
    leading=18,
)

# 添加中文内容
title = Paragraph("中文标题", chinese_style)
story.append(title)
story.append(Spacer(1, 12))

content = Paragraph("这是包含中文的正文内容。", chinese_style)
story.append(content)

# 构建 PDF
doc.build(story)
```

### Extracting Chinese Text

```python
import pdfplumber

# pdfplumber handles Chinese characters correctly
with pdfplumber.open("chinese_doc.pdf") as pdf:
    for page in pdf.pages:
        text = page.extract_text()
        print(text)  # Chinese characters will display correctly
```

### OCR for Scanned Chinese Documents

```python
import pytesseract
from pdf2image import convert_from_path

# Convert PDF to images
images = convert_from_path('chinese_scan.pdf')

# OCR with Chinese language pack
text = ""
for image in images:
    # Use 'chi_sim' for Simplified or 'chi_tra' for Traditional Chinese
    text += pytesseract.image_to_string(image, lang='chi_sim')
```

### Important Notes for Chinese Support

1. **Font Registration (ReportLab)**: 
   - **必须**使用 `pdfmetrics.registerFont()` 注册中文字体
   - 在所有使用中文的 `ParagraphStyle` 中设置 `fontName='ChineseFont'`
   - macOS 通常内置 PingFang 字体，Linux 需安装 `fonts-noto-cjk`，Windows 内置微软雅黑
   
2. **Encoding**: 
   - Python 脚本使用 UTF-8 编码保存
   - 读写文本文件时指定 `encoding='utf-8'`

3. **OCR Languages**: 
   - 安装 Tesseract 中文语言包：`chi_sim`（简体）或 `chi_tra`（繁体）
   - macOS: `brew install tesseract-lang`
   - Linux: `sudo apt-get install tesseract-ocr-chi-sim`

4. **Text Extraction**: 
   - `pdfplumber` 和 `pypdf` 可直接提取中文，无需特殊配置
   - 扫描型 PDF 需要 OCR 处理

## Python Libraries

### pypdf - Basic Operations

#### Merge PDFs
```python
from pypdf import PdfWriter, PdfReader

writer = PdfWriter()
for pdf_file in ["doc1.pdf", "doc2.pdf", "doc3.pdf"]:
    reader = PdfReader(pdf_file)
    for page in reader.pages:
        writer.add_page(page)

with open("merged.pdf", "wb") as output:
    writer.write(output)
```

#### Split PDF
```python
reader = PdfReader("input.pdf")
for i, page in enumerate(reader.pages):
    writer = PdfWriter()
    writer.add_page(page)
    with open(f"page_{i+1}.pdf", "wb") as output:
        writer.write(output)
```

#### Extract Metadata
```python
reader = PdfReader("document.pdf")
meta = reader.metadata
print(f"Title: {meta.title}")
print(f"Author: {meta.author}")
print(f"Subject: {meta.subject}")
print(f"Creator: {meta.creator}")
```

#### Rotate Pages
```python
reader = PdfReader("input.pdf")
writer = PdfWriter()

page = reader.pages[0]
page.rotate(90)  # Rotate 90 degrees clockwise
writer.add_page(page)

with open("rotated.pdf", "wb") as output:
    writer.write(output)
```

### pdfplumber - Text and Table Extraction

#### Extract Text with Layout
```python
import pdfplumber

with pdfplumber.open("document.pdf") as pdf:
    for page in pdf.pages:
        text = page.extract_text()
        print(text)
```

#### Extract Tables
```python
with pdfplumber.open("document.pdf") as pdf:
    for i, page in enumerate(pdf.pages):
        tables = page.extract_tables()
        for j, table in enumerate(tables):
            print(f"Table {j+1} on page {i+1}:")
            for row in table:
                print(row)
```

#### Advanced Table Extraction
```python
import pandas as pd

with pdfplumber.open("document.pdf") as pdf:
    all_tables = []
    for page in pdf.pages:
        tables = page.extract_tables()
        for table in tables:
            if table:  # Check if table is not empty
                df = pd.DataFrame(table[1:], columns=table[0])
                all_tables.append(df)

# Combine all tables
if all_tables:
    combined_df = pd.concat(all_tables, ignore_index=True)
    combined_df.to_excel("extracted_tables.xlsx", index=False)
```

### reportlab - Create PDFs

#### Basic PDF Creation
```python
from reportlab.lib.pagesizes import letter
from reportlab.pdfgen import canvas

c = canvas.Canvas("hello.pdf", pagesize=letter)
width, height = letter

# Add text
c.drawString(100, height - 100, "Hello World!")
c.drawString(100, height - 120, "This is a PDF created with reportlab")

# Add a line
c.line(100, height - 140, 400, height - 140)

# Save
c.save()
```

#### Create PDF with Multiple Pages
```python
from reportlab.lib.pagesizes import letter
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, PageBreak
from reportlab.lib.styles import getSampleStyleSheet

doc = SimpleDocTemplate("report.pdf", pagesize=letter)
styles = getSampleStyleSheet()
story = []

# Add content
title = Paragraph("Report Title", styles['Title'])
story.append(title)
story.append(Spacer(1, 12))

body = Paragraph("This is the body of the report. " * 20, styles['Normal'])
story.append(body)
story.append(PageBreak())

# Page 2
story.append(Paragraph("Page 2", styles['Heading1']))
story.append(Paragraph("Content for page 2", styles['Normal']))

# Build PDF
doc.build(story)
```

## Command-Line Tools

### pdftotext (poppler-utils)
```bash
# Extract text
pdftotext input.pdf output.txt

# Extract text preserving layout
pdftotext -layout input.pdf output.txt

# Extract specific pages
pdftotext -f 1 -l 5 input.pdf output.txt  # Pages 1-5
```

### qpdf
```bash
# Merge PDFs
qpdf --empty --pages file1.pdf file2.pdf -- merged.pdf

# Split pages
qpdf input.pdf --pages . 1-5 -- pages1-5.pdf
qpdf input.pdf --pages . 6-10 -- pages6-10.pdf

# Rotate pages
qpdf input.pdf output.pdf --rotate=+90:1  # Rotate page 1 by 90 degrees

# Remove password
qpdf --password=mypassword --decrypt encrypted.pdf decrypted.pdf
```

### pdftk (if available)
```bash
# Merge
pdftk file1.pdf file2.pdf cat output merged.pdf

# Split
pdftk input.pdf burst

# Rotate
pdftk input.pdf rotate 1east output rotated.pdf
```

## Common Tasks

### Extract Text from Scanned PDFs
```python
# Requires: pip install pytesseract pdf2image
import pytesseract
from pdf2image import convert_from_path

# Convert PDF to images
images = convert_from_path('scanned.pdf')

# OCR each page
text = ""
for i, image in enumerate(images):
    text += f"Page {i+1}:\n"
    text += pytesseract.image_to_string(image)
    text += "\n\n"

print(text)
```

### Add Watermark
```python
from pypdf import PdfReader, PdfWriter

# Create watermark (or load existing)
watermark = PdfReader("watermark.pdf").pages[0]

# Apply to all pages
reader = PdfReader("document.pdf")
writer = PdfWriter()

for page in reader.pages:
    page.merge_page(watermark)
    writer.add_page(page)

with open("watermarked.pdf", "wb") as output:
    writer.write(output)
```

### Extract Images
```bash
# Using pdfimages (poppler-utils)
pdfimages -j input.pdf output_prefix

# This extracts all images as output_prefix-000.jpg, output_prefix-001.jpg, etc.
```

### Password Protection
```python
from pypdf import PdfReader, PdfWriter

reader = PdfReader("input.pdf")
writer = PdfWriter()

for page in reader.pages:
    writer.add_page(page)

# Add password
writer.encrypt("userpassword", "ownerpassword")

with open("encrypted.pdf", "wb") as output:
    writer.write(output)
```

## Quick Reference

| Task | Best Tool | Command/Code |
|------|-----------|--------------|
| Merge PDFs | pypdf | `writer.add_page(page)` |
| Split PDFs | pypdf | One page per file |
| Extract text | pdfplumber | `page.extract_text()` |
| Extract tables | pdfplumber | `page.extract_tables()` |
| Create PDFs | reportlab | Canvas or Platypus |
| Command line merge | qpdf | `qpdf --empty --pages ...` |
| OCR scanned PDFs | pytesseract | Convert to image first |
| Fill PDF forms | pdf-lib or pypdf (see forms.md) | See forms.md |

## Next Steps

- For advanced pypdfium2 usage, see reference.md
- For JavaScript libraries (pdf-lib), see reference.md
- If you need to fill out a PDF form, follow the instructions in forms.md
- For troubleshooting guides, see reference.md

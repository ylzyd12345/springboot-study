#!/usr/bin/env python3
"""
Create PDF documents with Chinese text using ReportLab.
This script demonstrates how to use Chinese fonts in PDF creation.
"""

import sys
import os
from reportlab.lib.pagesizes import letter, A4
from reportlab.pdfgen import canvas
from reportlab.platypus import SimpleDocTemplate, Paragraph, Spacer, Table, TableStyle
from reportlab.lib.styles import getSampleStyleSheet, ParagraphStyle
from reportlab.lib import colors
from reportlab.pdfbase import pdfmetrics
from reportlab.pdfbase.ttfonts import TTFont
from reportlab.lib.units import inch


def find_chinese_font():
    """
    Find available Chinese fonts on the system.
    Returns the font name and path, or None if no Chinese font is found.
    """
    # Common Chinese font locations
    font_paths = {
        'darwin': [  # macOS
            '/System/Library/Fonts/PingFang.ttc',
            '/System/Library/Fonts/STHeiti Light.ttc',
            '/System/Library/Fonts/Supplemental/Songti.ttc',
            '/Library/Fonts/Arial Unicode.ttf',
        ],
        'linux': [  # Linux
            '/usr/share/fonts/truetype/noto/NotoSansCJK-Regular.ttc',
            '/usr/share/fonts/opentype/noto/NotoSansCJK-Regular.ttc',
            '/usr/share/fonts/truetype/arphic/uming.ttc',
            '/usr/share/fonts/truetype/wqy/wqy-microhei.ttc',
            '/usr/share/fonts/truetype/droid/DroidSansFallbackFull.ttf',
        ],
        'win32': [  # Windows
            'C:\\Windows\\Fonts\\simhei.ttf',
            'C:\\Windows\\Fonts\\simsun.ttc',
            'C:\\Windows\\Fonts\\msyh.ttc',
        ]
    }
    
    platform = sys.platform
    if platform not in font_paths:
        platform = 'linux'  # Default to linux paths
    
    for font_path in font_paths[platform]:
        if os.path.exists(font_path):
            font_name = os.path.basename(font_path).split('.')[0]
            return font_name, font_path
    
    return None, None


def register_chinese_font():
    """
    Register a Chinese font with ReportLab.
    Returns the registered font name, or None if registration fails.
    """
    font_name, font_path = find_chinese_font()
    
    if font_path is None:
        print("WARNING: No Chinese font found on the system.")
        print("Chinese characters may not display correctly.")
        print("Please install a Chinese font:")
        print("  macOS: Fonts are pre-installed")
        print("  Linux: sudo apt-get install fonts-noto-cjk")
        print("  Windows: Chinese fonts are pre-installed")
        return None
    
    try:
        # Register the font
        pdfmetrics.registerFont(TTFont('ChineseFont', font_path))
        print(f"Registered Chinese font: {font_name} from {font_path}")
        return 'ChineseFont'
    except Exception as e:
        print(f"Failed to register Chinese font: {e}")
        return None


def create_simple_pdf_with_chinese(output_path, text_content):
    """
    Create a simple PDF with Chinese text using canvas.
    
    Args:
        output_path: Path to save the PDF
        text_content: Chinese text to include in the PDF
    """
    font_name = register_chinese_font()
    
    c = canvas.Canvas(output_path, pagesize=A4)
    width, height = A4
    
    # Set font (use Chinese font if available, otherwise use default)
    if font_name:
        c.setFont(font_name, 16)
    else:
        c.setFont("Helvetica", 16)
    
    # Add title
    c.drawString(100, height - 100, "PDF 中文字符测试")
    
    # Add content
    if font_name:
        c.setFont(font_name, 12)
    else:
        c.setFont("Helvetica", 12)
    
    y_position = height - 150
    lines = text_content.split('\n')
    for line in lines:
        if y_position < 50:  # Start new page if needed
            c.showPage()
            if font_name:
                c.setFont(font_name, 12)
            else:
                c.setFont("Helvetica", 12)
            y_position = height - 50
        
        c.drawString(100, y_position, line)
        y_position -= 20
    
    c.save()
    print(f"Created PDF with Chinese text: {output_path}")


def create_complex_pdf_with_chinese(output_path, title, paragraphs, table_data=None):
    """
    Create a complex PDF document with Chinese text using Platypus.
    
    Args:
        output_path: Path to save the PDF
        title: Document title (Chinese)
        paragraphs: List of paragraph texts (Chinese)
        table_data: Optional list of lists for table data
    """
    font_name = register_chinese_font()
    
    doc = SimpleDocTemplate(output_path, pagesize=A4)
    story = []
    
    # Get default styles
    styles = getSampleStyleSheet()
    
    # Create custom styles with Chinese font
    if font_name:
        title_style = ParagraphStyle(
            'ChineseTitle',
            parent=styles['Title'],
            fontName=font_name,
            fontSize=24,
            spaceAfter=30,
        )
        normal_style = ParagraphStyle(
            'ChineseNormal',
            parent=styles['Normal'],
            fontName=font_name,
            fontSize=12,
            spaceAfter=12,
        )
    else:
        title_style = styles['Title']
        normal_style = styles['Normal']
    
    # Add title
    story.append(Paragraph(title, title_style))
    story.append(Spacer(1, 0.2 * inch))
    
    # Add paragraphs
    for para in paragraphs:
        story.append(Paragraph(para, normal_style))
        story.append(Spacer(1, 0.1 * inch))
    
    # Add table if provided
    if table_data:
        table = Table(table_data)
        table.setStyle(TableStyle([
            ('BACKGROUND', (0, 0), (-1, 0), colors.grey),
            ('TEXTCOLOR', (0, 0), (-1, 0), colors.whitesmoke),
            ('ALIGN', (0, 0), (-1, -1), 'CENTER'),
            ('FONTNAME', (0, 0), (-1, -1), font_name if font_name else 'Helvetica'),
            ('FONTSIZE', (0, 0), (-1, 0), 14),
            ('BOTTOMPADDING', (0, 0), (-1, 0), 12),
            ('BACKGROUND', (0, 1), (-1, -1), colors.beige),
            ('GRID', (0, 0), (-1, -1), 1, colors.black),
        ]))
        story.append(Spacer(1, 0.2 * inch))
        story.append(table)
    
    # Build PDF
    doc.build(story)
    print(f"Created complex PDF with Chinese text: {output_path}")


def main():
    """Main function demonstrating Chinese text support."""
    if len(sys.argv) < 2:
        print("Usage: create_pdf_with_chinese.py [output.pdf] [optional: text content]")
        print("\nCreating demo PDF with default Chinese content...")
        output_path = "chinese_test.pdf"
        text_content = "这是一个测试文档\n包含中文字符\n用于验证PDF创建功能"
    else:
        output_path = sys.argv[1]
        if len(sys.argv) >= 3:
            text_content = sys.argv[2]
        else:
            text_content = "这是一个测试文档\n包含中文字符\n用于验证PDF创建功能"
    
    # Create simple PDF
    simple_output = output_path.replace('.pdf', '_simple.pdf')
    create_simple_pdf_with_chinese(simple_output, text_content)
    
    # Create complex PDF
    complex_output = output_path.replace('.pdf', '_complex.pdf')
    title = "中文PDF文档测试报告"
    paragraphs = [
        "这是第一段测试内容。本文档用于验证iFlow CLI的PDF工作流对中文字符的支持能力。",
        "第二段包含更多内容。我们测试了文本提取、PDF创建、表格处理等多个功能模块。",
        "所有功能都应该能够正确处理中文字符，包括简体中文和繁体中文。",
    ]
    table_data = [
        ['功能', '状态', '备注'],
        ['文本提取', '✓', '支持UTF-8编码'],
        ['PDF创建', '✓', '需要中文字体'],
        ['表格处理', '✓', '完全支持'],
        ['OCR识别', '✓', '需要中文语言包'],
    ]
    create_complex_pdf_with_chinese(complex_output, title, paragraphs, table_data)
    
    print("\n✓ PDF creation with Chinese characters completed successfully!")
    print(f"  Simple PDF: {simple_output}")
    print(f"  Complex PDF: {complex_output}")


if __name__ == "__main__":
    main()


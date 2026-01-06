---
name: brand-guidelines
description: 应用Anthropic官方品牌色彩和排版到任何可能受益于Anthropic外观和感觉的工件。在品牌色彩或样式指南、视觉格式或公司设计标准适用时使用。
license: LICENSE.txt中的完整条款
workflow_trigger: /brand-guidelines
agent_path: .iflow/agents/brand-guidelines
---

# Anthropic品牌样式

## 概述

要访问Anthropic的官方品牌标识和样式资源，请使用此技能。

**关键词**: 品牌、企业标识、视觉标识、后处理、样式、品牌色彩、排版、Anthropic品牌、视觉格式、视觉设计

## 品牌指南

### 色彩

**主要色彩:**

- 深色: `#141413` - 主要文本和深色背景
- 浅色: `#faf9f5` - 浅色背景和深色上的文本
- 中灰: `#b0aea5` - 次要元素
- 浅灰: `#e8e6dc` - 细微背景

**强调色彩:**

- 橙色: `#d97757` - 主要强调色
- 蓝色: `#6a9bcc` - 次要强调色
- 绿色: `#788c5d` - 第三强调色

### 排版

- **标题**: Poppins (使用Arial作为备选)
- **正文**: Lora (使用Georgia作为备选)
- **注意**: 为获得最佳效果，字体应在您的环境中预安装

## 功能

### 智能字体应用

- 为标题(24pt及以上)应用Poppins字体
- 为正文应用Lora字体
- 如果自定义字体不可用，自动备选到Arial/Georgia
- 在所有系统上保持可读性

### 文本样式

- 标题(24pt+): Poppins字体
- 正文: Lora字体
- 基于背景的智能色彩选择
- 保留文本层次结构和格式

### 形状和强调色彩

- 非文本形状使用强调色彩
- 循环使用橙色、蓝色和绿色强调色
- 在保持品牌一致性的同时维持视觉趣味

## 技术细节

### 字体管理

- 在可用时使用系统安装的Poppins和Lora字体
- 自动备选到Arial(标题)和Georgia(正文)
- 无需字体安装 - 使用现有系统字体
- 为获得最佳效果，请在您的环境中预安装Poppins和Lora字体

### 色彩应用

- 使用RGB色彩值实现精确的品牌匹配
- 通过python-pptx的RGBColor类应用
- 在不同系统间保持色彩保真度
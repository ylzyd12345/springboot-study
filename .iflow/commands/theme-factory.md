---
name: theme-factory
description: 专业主题工厂工具，为演示文稿和文档提供10种预设主题和自定义主题生成功能
license: Complete terms in LICENSE.txt
workflow_trigger: /theme-factory
agent_path: .iflow/agents/theme-factory
---

# Workflow Trigger Mechanism

## Command Activation

When the user invokes the `/theme-factory` command, the system **MUST** execute the following automated workflow:

### 1. Environment Setup

**Set working directory:**
```bash
cd .iflow/agents/theme-factory
```

**All file paths are relative to this agent directory:**
- `theme-showcase.pdf` → `.iflow/agents/theme-factory/theme-showcase.pdf`
- `themes/*.md` → `.iflow/agents/theme-factory/themes/{theme-name}.md`

### 2. Workflow Decision Logic

**Immediately determine task type and execute appropriate workflow:**

| User Request | Workflow Action | Required Files |
|-------------|-----------------|----------------|
| Show available themes | Display theme-showcase.pdf | `theme-showcase.pdf` |
| Apply specific theme | Read theme definition and apply | `themes/{theme-name}.md` |
| Create custom theme | Generate new theme based on requirements | Reference `themes/` for format |
| List themes | Enumerate 10 available themes | None |

### 3. Automatic Dependency Management

**Dependency detection and installation strategy:**

The skill follows a "lazy installation" approach:
1. **Assume dependencies are installed** - Execute directly without pre-checks
2. **Install on failure** - Only install if command fails
3. **One-time setup** - Once installed, dependencies persist across sessions

**Installation commands by platform:**

```bash
# No external dependencies required
# Theme files are static resources (markdown and PDF)
```

### 4. Execution Flow

**Standard execution pattern:**

```
User: /theme-factory [request]
  ↓
System: Parse request type
  ↓
System: cd to agent_path
  ↓
System: Determine workflow (show/apply/create)
  ↓
System: If "show" → Display theme-showcase.pdf
System: If "apply" → Read theme file and apply to artifact
System: If "create" → Generate custom theme
  ↓
System: Return result to user
```

### 5. Critical Execution Rules

When executing any part of the THEME-FACTORY workflow, the system **MUST** adhere to these rules:

1. **ALWAYS show theme-showcase.pdf first** - Let users visually choose before applying
2. **Wait for explicit confirmation** - Never auto-apply a theme without user selection
3. **Read complete theme file** - Load all color/font specifications before applying
4. **Maintain consistency** - Apply theme uniformly across all artifact elements
5. **Custom theme validation** - Show generated theme for review before application
6. **Preserve artifact structure** - Only modify styling, not content or layout

## Skill Capabilities

This skill provides a comprehensive theme application toolkit with support for:

- **Theme Showcase**: Visual PDF gallery of 10 professional themes
- **Pre-set Themes**: 10 curated themes with cohesive color palettes and font pairings
  - Ocean Depths, Sunset Boulevard, Forest Canopy
  - Modern Minimalist, Golden Hour, Arctic Frost
  - Desert Rose, Tech Innovation, Botanical Garden, Midnight Galaxy
- **Theme Application**: Apply selected theme to slides, documents, reports, HTML pages
- **Custom Theme Generation**: Create new themes on-the-fly based on user requirements
- **Color Palette Management**: Hex codes for consistent color application
- **Font Pairing**: Complementary header and body text font combinations
- **Artifact Styling**: Professional styling for presentations, docs, and web content

## Overview

**Original Skill Description:** Toolkit for styling artifacts with a theme. These artifacts can be slides, docs, reportings, HTML landing pages, etc. There are 10 pre-set themes with colors/fonts that you can apply to any artifact that has been creating, or can generate a new theme on-the-fly.

# Theme Factory Skill

This skill provides a curated collection of professional font and color themes, each with carefully selected color palettes and font pairings. Once a theme is chosen, it can be applied to any artifact.

## Purpose

To apply consistent, professional styling to presentation slide decks, use this skill. Each theme includes:
- A cohesive color palette with hex codes
- Complementary font pairings for headers and body text
- A distinct visual identity suitable for different contexts and audiences

## Usage Instructions

To apply styling to a slide deck or other artifact:

1. **Show the theme showcase**: Display the `theme-showcase.pdf` file to allow users to see all available themes visually. Do not make any modifications to it; simply show the file for viewing.
2. **Ask for their choice**: Ask which theme to apply to the deck
3. **Wait for selection**: Get explicit confirmation about the chosen theme
4. **Apply the theme**: Once a theme has been chosen, apply the selected theme's colors and fonts to the deck/artifact

## Themes Available

The following 10 themes are available, each showcased in `theme-showcase.pdf`:

1. **Ocean Depths** - Professional and calming maritime theme
2. **Sunset Boulevard** - Warm and vibrant sunset colors
3. **Forest Canopy** - Natural and grounded earth tones
4. **Modern Minimalist** - Clean and contemporary grayscale
5. **Golden Hour** - Rich and warm autumnal palette
6. **Arctic Frost** - Cool and crisp winter-inspired theme
7. **Desert Rose** - Soft and sophisticated dusty tones
8. **Tech Innovation** - Bold and modern tech aesthetic
9. **Botanical Garden** - Fresh and organic garden colors
10. **Midnight Galaxy** - Dramatic and cosmic deep tones

## Theme Details

Each theme is defined in the `themes/` directory with complete specifications including:
- Cohesive color palette with hex codes
- Complementary font pairings for headers and body text
- Distinct visual identity suitable for different contexts and audiences

## Application Process

After a preferred theme is selected:
1. Read the corresponding theme file from the `themes/` directory
2. Apply the specified colors and fonts consistently throughout the deck
3. Ensure proper contrast and readability
4. Maintain the theme's visual identity across all slides

## Create your Own Theme

To handle cases where none of the existing themes work for an artifact, create a custom theme. Based on provided inputs, generate a new theme similar to the ones above. Give the theme a similar name describing what the font/color combinations represent. Use any basic description provided to choose appropriate colors/fonts. After generating the theme, show it for review and verification. Following that, apply the theme as described above.


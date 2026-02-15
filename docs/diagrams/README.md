# 📊 Documentation Diagrams

This directory contains all diagrams used in the Go2Office documentation.

## 📁 Structure

```
diagrams/
├── images/          # Generated PNG files (📊 Visible in docs)
├── puml/            # PlantUML source files
└── README.md        # This file
```

## 🎨 Generating PNG Images

All diagrams are created with PlantUML and rendered as PNG images.

### Quick Generate (Recommended)

```bash
# From project root
./scripts/generate-pngs.sh
```

This will:
1. Process all `.puml` files in `docs/diagrams/puml/`
2. Generate PNG images in `docs/diagrams/images/`
3. Make them ready to display in documentation

### Prerequisites

**Option 1: Use PlantUML JAR (included in repo)**
```bash
# Check Java is installed
java -version

# If not installed:
# macOS: brew install openjdk
# Linux: apt-get install default-jre
```

**Option 2: Install PlantUML command**
```bash
# macOS
brew install plantuml

# Ubuntu/Debian
sudo apt-get install plantuml

# Arch Linux
sudo pacman -S plantuml
```

### Manual Generation

To generate a single diagram:

```bash
# Using JAR
java -jar scripts/plantuml.jar -tpng docs/diagrams/puml/system-architecture.puml -o docs/diagrams/images/

# Using command
plantuml -tpng docs/diagrams/puml/system-architecture.puml -o docs/diagrams/images/
```

## 📊 Available Diagrams

### Architecture Diagrams
- `system-architecture.png` - Three-layer architecture overview
- `components.png` - Component interaction diagram
- `data-flow.png` - Data flow through layers
- `state-management.png` - State management pattern
- `dependency-injection.png` - Hilt DI setup
- `database-schema.png` - Room database schema
- `navigation.png` - App navigation flow
- `testing-pyramid.png` - Testing strategy

### Business Logic Diagrams
- `requirements-calculation.png` - Monthly requirements formula
- `suggestion-algorithm.png` - Smart day suggestions
- `work-hours-window.png` - Hour tracking rules
- `session-aggregation.png` - Session to daily aggregation
- `exclusion-rules.png` - Holiday/vacation logic
- `progress-tracking.png` - Progress calculation

### Sequence Diagrams
- `auto-detection-complete.png` - Complete geofence flow
- `manual-entry.png` - Manual day entry flow
- `dashboard-load.png` - Dashboard initialization
- `load-holidays.png` - Holiday API flow
- `settings-update.png` - Settings change flow

### User Guide Diagrams
- `onboarding-flow.png` - First-time setup
- `new-month.png` - New month planning
- `mid-month.png` - Mid-month check
- `month-end.png` - Month-end completion

## 🔧 Editing Diagrams

1. Edit the `.puml` file in `docs/diagrams/puml/`
2. Run `./scripts/generate-pngs.sh`
3. Commit both the `.puml` source and generated `.png`

## 🌐 Online Viewing

Don't have PlantUML installed? View/edit online:

1. Copy PlantUML code from any documentation file
2. Go to http://www.plantuml.com/plantuml/uml/
3. Paste and view/edit the diagram
4. Export as PNG if needed

## 📝 PlantUML Syntax Reference

- [PlantUML Guide](https://plantuml.com/guide)
- [Component Diagrams](https://plantuml.com/component-diagram)
- [Sequence Diagrams](https://plantuml.com/sequence-diagram)
- [Class Diagrams](https://plantuml.com/class-diagram)

## 🎯 Best Practices

1. **Keep it simple** - Diagrams should clarify, not complicate
2. **Consistent styling** - Use same colors/themes across diagrams
3. **Version control both** - Commit `.puml` source and `.png` output
4. **Descriptive names** - Use clear, descriptive file names
5. **Add captions** - Include alt text in markdown

## 🤝 Contributing

When adding new diagrams:

1. Create `.puml` file in `docs/diagrams/puml/`
2. Generate PNG: `./scripts/generate-pngs.sh`
3. Reference in documentation: `![Description](../diagrams/images/your-diagram.png)`
4. Commit both files

---

**Need help?** See [Technical Guide](../technical/README.md) for more details.


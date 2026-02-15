#!/bin/bash
# Generate PNG images from PlantUML diagrams

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
DOCS_DIR="$SCRIPT_DIR/../docs"
DIAGRAMS_DIR="$DOCS_DIR/diagrams/images"
PLANTUML_JAR="$SCRIPT_DIR/plantuml.jar"

echo "🎨 PlantUML PNG Generator for Go2Office Documentation"
echo "======================================================"

# Create images directory
mkdir -p "$DIAGRAMS_DIR"
echo "✅ Created diagrams directory: $DIAGRAMS_DIR"

# Check for PlantUML
if command -v plantuml &> /dev/null; then
    PLANTUML_CMD="plantuml"
    echo "✅ Found PlantUML command"
elif [ -f "$PLANTUML_JAR" ]; then
    PLANTUML_CMD="java -jar $PLANTUML_JAR"
    echo "✅ Using PlantUML JAR: $PLANTUML_JAR"
else
    echo "❌ PlantUML not found!"
    echo ""
    echo "Please install PlantUML:"
    echo "  macOS:  brew install plantuml"
    echo "  Linux:  apt-get install plantuml  OR  yum install plantuml"
    echo "  Manual: Download from https://plantuml.com/download"
    echo "          and place plantuml.jar in scripts/ directory"
    exit 1
fi

# Function to extract and generate PlantUML diagrams
generate_diagram() {
    local source_file="$1"
    local diagram_name="$2"
    local temp_file="$DIAGRAMS_DIR/${diagram_name}.puml"
    local output_file="$DIAGRAMS_DIR/${diagram_name}.png"

    # Extract PlantUML code between @startuml and @enduml
    sed -n '/@startuml/,/@enduml/p' "$source_file" | \
        grep -A 9999 "@startuml $diagram_name" | \
        sed '/@enduml/q' > "$temp_file" 2>/dev/null || return 1

    if [ ! -s "$temp_file" ]; then
        return 1
    fi

    # Generate PNG
    $PLANTUML_CMD -tpng "$temp_file" -o "$(pwd)/$DIAGRAMS_DIR" 2>/dev/null

    if [ -f "$output_file" ]; then
        echo "  ✅ Generated: ${diagram_name}.png"
        rm "$temp_file"
        return 0
    else
        rm "$temp_file"
        return 1
    fi
}

echo ""
echo "📊 Generating Architecture Diagrams..."
echo "---------------------------------------"

# Architecture diagrams
generate_diagram "$DOCS_DIR/architecture/README.md" "system-architecture"
generate_diagram "$DOCS_DIR/architecture/README.md" "components"
generate_diagram "$DOCS_DIR/architecture/README.md" "data-flow"
generate_diagram "$DOCS_DIR/architecture/README.md" "state-management"
generate_diagram "$DOCS_DIR/architecture/README.md" "dependency-injection"
generate_diagram "$DOCS_DIR/architecture/README.md" "database-schema"
generate_diagram "$DOCS_DIR/architecture/README.md" "navigation"
generate_diagram "$DOCS_DIR/architecture/README.md" "testing-pyramid"

echo ""
echo "📊 Generating Business Logic Diagrams..."
echo "---------------------------------------"

# Business logic diagrams
generate_diagram "$DOCS_DIR/business/README.md" "requirements-calculation"
generate_diagram "$DOCS_DIR/business/README.md" "suggestion-algorithm"
generate_diagram "$DOCS_DIR/business/README.md" "work-hours-window"
generate_diagram "$DOCS_DIR/business/README.md" "session-aggregation"
generate_diagram "$DOCS_DIR/business/README.md" "exclusion-rules"
generate_diagram "$DOCS_DIR/business/README.md" "holiday-impact"
generate_diagram "$DOCS_DIR/business/README.md" "progress-tracking"

echo ""
echo "📊 Generating Sequence Diagrams..."
echo "---------------------------------------"

# Sequence diagrams
generate_diagram "$DOCS_DIR/diagrams/use-case-flows.md" "auto-detection-complete"
generate_diagram "$DOCS_DIR/diagrams/use-case-flows.md" "manual-entry"
generate_diagram "$DOCS_DIR/diagrams/use-case-flows.md" "dashboard-load"
generate_diagram "$DOCS_DIR/diagrams/use-case-flows.md" "load-holidays"
generate_diagram "$DOCS_DIR/diagrams/use-case-flows.md" "settings-update"
generate_diagram "$DOCS_DIR/diagrams/use-case-flows.md" "geofence-processing"
generate_diagram "$DOCS_DIR/diagrams/use-case-flows.md" "error-handling"

echo ""
echo "📊 Generating User Guide Diagrams..."
echo "---------------------------------------"

# User guide diagrams
generate_diagram "$DOCS_DIR/user-guide/README.md" "onboarding-flow"
generate_diagram "$DOCS_DIR/user-guide/README.md" "new-month"
generate_diagram "$DOCS_DIR/user-guide/README.md" "mid-month"
generate_diagram "$DOCS_DIR/user-guide/README.md" "month-end"

echo ""
echo "📊 Generating Technical Diagrams..."
echo "---------------------------------------"

# Technical diagrams
generate_diagram "$DOCS_DIR/technical/README.md" "tech-stack"

echo ""
echo "======================================================"
echo "✅ PlantUML PNG generation complete!"
echo ""
echo "Generated images are in: $DIAGRAMS_DIR"
echo ""
ls -lh "$DIAGRAMS_DIR"/*.png 2>/dev/null | wc -l | xargs echo "Total PNG files:"
echo ""
echo "Next step: Update markdown files to reference PNG images"
echo "======================================================"


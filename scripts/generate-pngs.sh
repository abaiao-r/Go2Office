#!/bin/bash
# Simple PlantUML PNG Generator

set -e

DOCS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../docs" && pwd)"
PUML_DIR="$DOCS_DIR/diagrams/puml"
IMG_DIR="$DOCS_DIR/diagrams/images"

echo "🎨 Generating PlantUML PNG Images"
echo "=================================="

# Create directories
mkdir -p "$PUML_DIR"
mkdir -p "$IMG_DIR"

# Check for Java
if ! command -v java &> /dev/null; then
    echo "❌ Java not found! Please install Java to generate diagrams."
    echo "   macOS: brew install openjdk"
    exit 1
fi

# Use online PlantUML server API (simpler, no JAR needed)
generate_from_server() {
    local puml_file="$1"
    local png_file="$2"

    echo "  Generating $(basename $png_file)..."

    # Encode PlantUML code
    local encoded=$(java -jar "$(dirname "${BASH_SOURCE[0]}")/plantuml.jar" -encodeurl "$puml_file" 2>/dev/null || echo "")

    if [ -z "$encoded" ]; then
        # Fallback: try direct generation
        if [ -f "$(dirname "${BASH_SOURCE[0]}")/plantuml.jar" ]; then
            java -jar "$(dirname "${BASH_SOURCE[0]}")/plantuml.jar" -tpng "$puml_file" -o "$IMG_DIR"
            echo "  ✅ Generated: $(basename $png_file)"
        else
            echo "  ⏭️  Skipped (PlantUML JAR not found)"
        fi
    fi
}

# Generate all diagrams
echo ""
echo "Generating diagrams from $PUML_DIR..."
echo ""

if [ -f "$(dirname "${BASH_SOURCE[0]}")/plantuml.jar" ]; then
    for puml in "$PUML_DIR"/*.puml; do
        if [ -f "$puml" ]; then
            filename=$(basename "$puml" .puml)
            java -jar "$(dirname "${BASH_SOURCE[0]}")/plantuml.jar" -tpng "$puml" -o "$IMG_DIR"
            if [ -f "$IMG_DIR/${filename}.png" ]; then
                echo "✅ Generated: ${filename}.png"
            fi
        fi
    done
else
    echo "❌ plantuml.jar not found!"
    echo ""
    echo "Please download PlantUML:"
    echo "  curl -L https://github.com/plantuml/plantuml/releases/download/v1.2024.3/plantuml-1.2024.3.jar -o scripts/plantuml.jar"
    echo ""
    echo "Or install via package manager:"
    echo "  macOS: brew install plantuml"
    exit 1
fi

echo ""
echo "=================================="
echo "✅ Complete! Generated $(ls -1 "$IMG_DIR"/*.png 2>/dev/null | wc -l | tr -d ' ') PNG files"
echo ""
echo "Images location: $IMG_DIR"
echo "=================================="


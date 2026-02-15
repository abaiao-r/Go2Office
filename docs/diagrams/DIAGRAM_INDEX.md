# 📊 Diagram Index

Quick reference guide for all PlantUML diagrams in the Go2Office documentation.

---

## 🏗️ Architecture Diagrams (8)

| Diagram | File | Description | Used In |
|---------|------|-------------|---------|
| **System Architecture** | [system-architecture.puml](puml/system-architecture.puml) | Three-layer Clean Architecture overview | [Architecture](../architecture/README.md#system-architecture) |
| **Components** | [components.puml](puml/components.puml) | Component interaction diagram | [Architecture](../architecture/README.md#component-diagram) |
| **Data Flow** | [data-flow.puml](puml/data-flow.puml) | Unidirectional data flow sequence | [Architecture](../architecture/README.md#data-flow) |
| **State Management** | [state-management.puml](puml/state-management.puml) | StateFlow pattern state machine | [Architecture](../architecture/README.md#state-management) |
| **Dependency Injection** | [dependency-injection.puml](puml/dependency-injection.puml) | Hilt DI structure | [Architecture](../architecture/README.md#dependency-injection) |
| **Database Schema** | [database-schema.puml](puml/database-schema.puml) | Room entities and relationships | [Architecture](../architecture/README.md#database-schema) |
| **Navigation** | [navigation.puml](puml/navigation.puml) | App navigation state machine | [Architecture](../architecture/README.md#navigation-flow) |
| **Testing Pyramid** | [testing-pyramid.puml](puml/testing-pyramid.puml) | Testing strategy | [Architecture](../architecture/README.md#testing-strategy) |

---

## 💼 Business Logic Diagrams (11)

| Diagram | File | Description | Used In |
|---------|------|-------------|---------|
| **Requirements Calculation** | [requirements-calculation.puml](puml/requirements-calculation.puml) | Monthly requirements formula | [Business](../business/README.md#required-days-calculation) |
| **Suggestion Algorithm** | [suggestion-algorithm.puml](puml/suggestion-algorithm.puml) | Smart day suggestions flowchart | [Business](../business/README.md#smart-suggestions-algorithm) |
| **Work Hours Window** | [work-hours-window.puml](puml/work-hours-window.puml) | 7am-7pm tracking rules | [Business](../business/README.md#work-hours-window) |
| **Session Aggregation** | [session-aggregation.puml](puml/session-aggregation.puml) | Multiple sessions → daily entry | [Business](../business/README.md#session-aggregation) |
| **Exclusion Rules** | [exclusion-rules.puml](puml/exclusion-rules.puml) | Weekend/holiday decision tree | [Business](../business/README.md#exclusion-rules) |
| **Holiday Impact** | [holiday-impact.puml](puml/holiday-impact.puml) | Requirement adjustments | [Business](../business/README.md#impact-on-requirements) |
| **Progress Tracking** | [progress-tracking.puml](puml/progress-tracking.puml) | Completion calculation | [Business](../business/README.md#completion-calculation) |
| **Insufficient Days** | [insufficient-days.puml](puml/insufficient-days.puml) | Edge case handling | [Business](../business/README.md#case-1-insufficient-remaining-days) |
| **Settings Validation** | [settings-validation.puml](puml/settings-validation.puml) | Input validation rules | [Business](../business/README.md#settings-validation) |
| **KPIs** | [kpis.puml](puml/kpis.puml) | Business metrics | [Business](../business/README.md#key-performance-indicators) |
| **Weekly Distribution** | [weekly-distribution.puml](puml/weekly-distribution.puml) | Week-by-week example | [Business](../business/README.md#example-scenario) |

---

## 🔄 Sequence Diagrams (7)

| Diagram | File | Description | Used In |
|---------|------|-------------|---------|
| **Auto-Detection Complete** | [auto-detection-complete.puml](puml/auto-detection-complete.puml) | Full geofence cycle | [Sequences](use-case-flows.md#auto-detection-flow) |
| **Manual Entry** | [manual-entry.puml](puml/manual-entry.puml) | Manual day logging | [Sequences](use-case-flows.md#manual-day-entry) |
| **Dashboard Load** | [dashboard-load.puml](puml/dashboard-load.puml) | Dashboard initialization | [Sequences](use-case-flows.md#monthly-dashboard-load) |
| **Load Holidays** | [load-holidays.puml](puml/load-holidays.puml) | Holiday API fetch | [Sequences](use-case-flows.md#holiday-management) |
| **Settings Update** | [settings-update.puml](puml/settings-update.puml) | Settings change flow | [Sequences](use-case-flows.md#settings-update) |
| **Geofence Processing** | [geofence-processing.puml](puml/geofence-processing.puml) | Background processing | [Sequences](use-case-flows.md#background-processing) |
| **Error Handling** | [error-handling.puml](puml/error-handling.puml) | Error recovery | [Sequences](use-case-flows.md#error-handling) |

---

## 👤 User Guide Diagrams (4)

| Diagram | File | Description | Used In |
|---------|------|-------------|---------|
| **Onboarding Flow** | [onboarding-flow.puml](puml/onboarding-flow.puml) | First-time setup | [User Guide](../user-guide/README.md#first-time-setup) |
| **New Month** | [new-month.puml](puml/new-month.puml) | Monthly planning | [User Guide](../user-guide/README.md#scenario-1-new-month-planning) |
| **Mid-Month Check** | [mid-month.puml](puml/mid-month.puml) | Progress check | [User Guide](../user-guide/README.md#scenario-2-mid-month-check-in) |
| **Month-End** | [month-end.puml](puml/month-end.puml) | Requirement completion | [User Guide](../user-guide/README.md#scenario-3-month-end-rush) |

---

## 🔧 Technical Diagrams (1)

| Diagram | File | Description | Used In |
|---------|------|-------------|---------|
| **Tech Stack** | [tech-stack.puml](puml/tech-stack.puml) | Technology overview | [Technical](../technical/README.md#architecture-overview) |

---

## 📊 Summary

**Total Diagrams**: 31 PlantUML source files

**By Category**:
- Architecture: 8 diagrams
- Business Logic: 11 diagrams
- Sequence Flows: 7 diagrams
- User Workflows: 4 diagrams
- Technical: 1 diagram

**File Organization**:
```
docs/diagrams/
├── images/           # PNG output (generated)
│   └── *.png        # 31 PNG files
└── puml/            # PlantUML source
    └── *.puml       # 31 source files
```

---

## 🎨 Diagram Types Used

| Type | Count | Purpose |
|------|-------|---------|
| Component Diagram | 3 | Show system components and dependencies |
| Sequence Diagram | 8 | Illustrate interactions over time |
| Activity Diagram | 10 | Show business logic flows |
| State Diagram | 2 | Display state transitions |
| Entity Diagram | 1 | Represent database schema |
| Object Diagram | 4 | Show example scenarios |
| Package Diagram | 3 | Organize architectural layers |

---

## 🚀 Quick Commands

```bash
# Generate all PNGs
./scripts/generate-pngs.sh

# Generate specific diagram
java -jar scripts/plantuml.jar -tpng docs/diagrams/puml/system-architecture.puml -o docs/diagrams/images/

# View online
# Copy .puml content to http://www.plantuml.com/plantuml/uml/
```

---

## 📖 Documentation Links

- [Architecture Guide](../architecture/README.md)
- [Business Logic](../business/README.md)
- [Sequence Flows](use-case-flows.md)
- [User Guide](../user-guide/README.md)
- [Technical Guide](../technical/README.md)
- [Diagram Generation Guide](README.md)


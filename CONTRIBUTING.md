# Contributing to Go2Office

Thank you for your interest in contributing to Go2Office! This document provides guidelines and instructions for contributing.

## 📋 Code of Conduct

- Be respectful and inclusive
- Provide constructive feedback
- Focus on the issue, not the person

## 🔄 Development Workflow

### 1. Fork & Clone

```bash
git clone https://github.com/YOUR_USERNAME/Go2Office.git
cd Go2Office
```

### 2. Set Up Development Environment

- Android Studio Arctic Fox or newer
- JDK 17
- Android SDK 34

### 3. Create a Branch

```bash
git checkout master
git pull origin master
git checkout -b feature/your-feature-name
```

**Branch naming conventions:**
- `feature/` - New features
- `bugfix/` - Bug fixes
- `hotfix/` - Urgent production fixes
- `refactor/` - Code refactoring
- `docs/` - Documentation updates
- `test/` - Test additions/updates

### 4. Make Changes

Follow these guidelines:
- Follow Kotlin coding conventions
- Write meaningful commit messages
- Add tests for new functionality
- Update documentation if needed

### 5. Commit Messages

Use conventional commits format:

```
type(scope): description

[optional body]

[optional footer]
```

**Types:**
- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation
- `style` - Formatting (no code change)
- `refactor` - Code refactoring
- `test` - Adding tests
- `chore` - Maintenance

**Examples:**
```
feat(dashboard): add monthly history button
fix(onboarding): persist settings after completion
docs(readme): add contributing section
test(viewmodel): add dashboard unit tests
```

### 6. Run Tests

Before pushing, ensure all tests pass:

```bash
# Unit tests
./gradlew testDebugUnitTest

# Lint checks
./gradlew lintDebug

# Build
./gradlew assembleDebug
```

### 7. Push & Create PR

```bash
git push origin feature/your-feature-name
```

Then create a Pull Request on GitHub targeting `master` branch.

## ✅ Pull Request Guidelines

### Before Submitting

- [ ] Tests pass locally
- [ ] No lint errors
- [ ] Code follows project style
- [ ] Documentation updated (if needed)
- [ ] Commit messages follow convention

### PR Description

Use the PR template and include:
- Clear description of changes
- Type of change (feature, fix, etc.)
- How it was tested
- Screenshots (if UI changes)
- Related issues

### Review Process

1. Automated CI checks run
2. Code review by maintainers
3. Address feedback if any
4. Merge after approval

## 🏗️ Project Structure

```
app/src/main/java/com/example/go2office/
├── data/           # Data layer (Room, repositories)
├── di/             # Dependency injection (Hilt modules)
├── domain/         # Business logic (use cases, models)
├── presentation/   # UI layer (screens, ViewModels)
├── service/        # Background services
└── util/           # Utility classes
```

## 🧪 Testing

### Unit Tests Location

```
app/src/test/java/com/example/go2office/
├── data/           # Repository tests
├── domain/         # Use case tests
├── presentation/   # ViewModel tests
├── ui/             # Compose UI tests (Robolectric)
└── util/           # Utility tests
```

### Test Naming Convention

```kotlin
@Test
fun `GIVEN precondition WHEN action THEN expected result`()
```

Example:
```kotlin
@Test
fun `GIVEN user has completed onboarding WHEN app starts THEN should navigate to dashboard`()
```

## 🐛 Reporting Bugs

Create an issue with:
- Clear title
- Steps to reproduce
- Expected vs actual behavior
- Android version
- Screenshots/logs if applicable

## 💡 Feature Requests

Create an issue with:
- Clear description of the feature
- Use case / why it's needed
- Possible implementation approach

## 📞 Questions?

Open a discussion or issue on GitHub.

---

Thank you for contributing! 🎉




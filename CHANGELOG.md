# Changelog

## [0.1.0] - 2023-08-15
### Added
- Initial project setup with Quarkus
- Added core dependencies (RESTEasy, Hibernate ORM Panache, PostgreSQL, OpenAPI, JWT, etc.)
- Created domain model entities: Usuario, PeriodoServico, Simulacao, PeriodoTempo
- Implemented repository layer for PeriodoServico
- Implemented service layer for retirement calculation logic
- Created REST resources for Usuario, PeriodoServico, and Simulacao entities
- Configured application.properties with database, logging, and development settings
- Added sample data import script for development environment

## [0.1.1] - 2023-08-16
### Changed
- Enabled DevServices for PostgreSQL database in development mode
- Added explicit PostgreSQL JDBC driver dependency
- Updated test configuration to use DevServices
- Fixed foreign key constraint issue in sample data import script by using explicit IDs

## [0.1.2] - 2023-08-17
### Added
- Modern and professional UI using Vue.js
- Responsive layout with Bootstrap 5
- User-friendly stepper for simulation process
- Dashboard with visualization components
- Profile management and simulation history
- Forms for entering personal data and service periods
- Rule selection interface with visual cues
- Results display with gauge visualization
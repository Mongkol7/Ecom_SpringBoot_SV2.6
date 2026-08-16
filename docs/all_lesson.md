នេះជាសេចក្ដីសង្ខេបនៃមេរៀន Spring Boot ទាំងអស់តាមមេរៀននីមួយៗ៖

### 01. Spring Core vs Spring Boot

- **Spring Core**: ជា Foundation Framework របស់ Java Enterprise Application ដែលមាន Feature សំខាន់ៗដូចជា IoC Container (Inversion of Control) និង Dependency Injection (DI) សម្រាប់គ្រប់គ្រង Objects (Beans)។ វាទាមទារការ Setup/Configuration ដោយដៃ (XML ឬ Java Config) និងត្រូវការ External Server (ដូចជា Tomcat) ដើម្បី Deploy។
- **Spring Boot**: ជា Tool Extension ដែលបង្កើតលើ Spring Core ដើម្បីធ្វើឱ្យការអភិវឌ្ឍរហ័ស (Production-Ready)। វាមាន Auto-Configuration, Starter Dependencies (ដូចជា `spring-boot-starter-web`, `spring-boot-starter-data-jpa`), និង Embedded Server (Tomcat) ស្រាប់។

### 02. Dependency vs Dependency Injection (DI)

- **Dependency**: កើតឡើងនៅពេលដែល Class មួយ (ឧ. Class A) ត្រូវការប្រើប្រាស់ Class មួយទៀត (ឧ. Class B) ដើម្បីដំណើរការ។
- **Dependency Injection (DI)**: ជាការប្រគល់ភារកិច្ចបង្កើត និង Inject Object ទៅឱ្យ Spring IoC Container ជាអ្នកគ្រប់គ្រងជំនួសឱ្យការប្រើ `new` ដោយផ្ទាល់នៅក្នុង Class។
- **អត្ថប្រយោជន៍នៃ DI**: ជួយឱ្យកូដ Loose Coupling, ងាយស្រួលធ្វើ Unit Testing (អាច Mock Object បាន), ងាយស្រួល Reusability, និងមានការគ្រប់គ្រង Object Lifecycle ច្បាស់លាស់។

### 03. Connect Spring with HTML

- **`@RestController` vs `@Controller`**: `@RestController` មិនអាចប្រើជាមួយ HTML ដោយផ្ទាល់បានទេ ព្រោះវា Return ជា Data/Text។ ចំណែក `@Controller` ត្រូវប្រើនៅពេលចង់ Return ឈ្មោះ HTML File (ឧ. `"index.html"`) ទៅកាន់ Browser។
- **Folder Structure សម្រាប់ Frontend**: Static Files (CSS, JavaScript) ត្រូវដាក់ក្នុង `src/main/resources/static/`។ រីឯ HTML Files ត្រូវដាក់ក្នុង `src/main/resources/templates/`។

### 04. Spring Boot Project Structure

- **គោលការណ៍ Separation of Concerns**: ការរៀបចំ Folder ឱ្យមានរបៀបរៀបរយ ជួយឱ្យកូដស្អាត (Clean Code), ងាយស្រួល Maintain, និង Scalable។
- **`src/main/java`**: សម្រាប់ទុក Backend Logic (Controllers, Services, Repositories, Models)។
- **`src/main/resources`**: រួមមាន `static/` (CSS, JS), `templates/` (HTML Views ប្រើជាមួយ Thymeleaf), និង `application.properties`។

### 05. MVC Architecture Pattern

- **Model**: គ្រប់គ្រង Data និង Business Logic (Entity, Repositories, Services)។
- **View**: ផ្នែក User Interface (HTML, CSS, JS) ដែលបង្ហាញទៅកាន់ User ដោយប្រើ Template Engine ដូចជា Thymeleaf។
- **Controller**: អ្នកកណ្ដាល (Middleman) ទទួល HTTP Request ពី User/Browser, ទាញយក Data ពី Model, ហើយផ្ញើ View ដែល Render រួចទៅឱ្យ User វិញ។

### 06. MVC (Array / Collection Handling)

- **ការបញ្ជូន Data ជា Array/List**: នៅក្នុង Controller គេប្រើ `Model.addAttribute("students", students)` ដើម្បីបញ្ជូន Collection នៃ Objects ទៅកាន់ Thymeleaf Template។
- **MVC Flow Process**: Browser $\rightarrow$ StudentController $\rightarrow$ Student (Model Object) $\rightarrow$ Thymeleaf View (Render ជា Table/List) $\rightarrow$ Browser។

### 08. RESTful API Implementation

- **`@RestController` & `@RequestMapping`**: ប្រើសម្រាប់បង្កើត RESTful Web Services ឆ្លើយតបជាទិន្នន័យ (JSON)។
- **HTTP Mapping Annotations**:
  - `@GetMapping`: ទាញយកទិន្នន័យ (GET)។
  - `@PostMapping`: បង្កើតទិន្នន័យថ្មី (POST)។
  - `@PutMapping("/{id}")`: ធ្វើបច្ចុប្បន្នភាពទិន្នន័យ (PUT) ដោយប្រើ `@PathVariable` និង `@RequestBody`។
  - `@DeleteMapping("/{id}")`: លុបទិន្នន័យ (DELETE)។
- **`ResponseEntity`**: ប្រើសម្រាប់គ្រប់គ្រង HTTP Status Codes ដូចជា `200 OK`, `404 Not Found`, និង `204 No Content`។

### 10. Repository & Service Folders (3-Layer Architecture)

- Architecture Pattern: Client/Browser $\rightarrow$ Controller Layer $\rightarrow$ Service Layer $\rightarrow$ Repository Layer $\rightarrow$ Database។
- **Repository Layer (`repository/`)**: ទទួលខុសត្រូវលើការទាក់ទងដោយផ្ទាល់ជាមួយ Database (CRUD operations, Custom SQL/JPQL) ដោយ Extends `JpaRepository`។
- **Service Layer (`service/`)**: ជា "ខួរក្បាល" នៃ Application សម្រាប់សរសេរ Business Logic, Validation rules, និង Calculations។

### 11. `@Autowired` & `@Service` Annotations

- **`@Service`**: ប្រកាស Class មួយជា Service Bean នៅក្នុង Spring Container សម្រាប់សរសេរ Business Logic។
- **`@Autowired`**: ប្រើសម្រាប់ធ្វើ Dependency Injection ដោយស្វ័យប្រវត្តិ។
- **Best Practice**: ការប្រើ **Constructor Injection** ជាមួយ `final` Fields ត្រូវបានលើកទឹកចិត្តជាង Field Injection ព្រោះវាផ្តល់ Immutability និងងាយស្រួលធ្វើ Unit Testing។

### 12. DB Management & Full CRUD Setup

- **Dependencies សំខាន់ៗ**: MySQL Driver, Spring Data JPA, JDBC API, Thymeleaf, និង Spring Web។
- **`application.properties` Config**: កំណត់ Database URL (`spring.datasource.url`), Username, Password, `spring.jpa.hibernate.ddl-auto=update`, និង `spring.jpa.show-sql=true`។
- **ការភ្ជាប់សមាសភាគទាំងអស់**: ប្រើ `@Entity` លើ Model Class, Extends `JpaRepository` លើ Repository Interface, ប្រើ `@Service` លើ Service Class, និងប្រើ `@RestController` / `@Autowired` លើ Controller Class ដើម្បីសរសេរ CRUD Complete Operations។

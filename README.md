# Clase 3 — React + TypeScript aplicado a migración desde JSP

Repositorio de práctica (30–40 min, extensión opcional hasta 45).

## Requisitos
- JDK 21 y Maven 3.9+ (backend, Spring Boot 3.3.5)
- Node.js `^20.19` o `>=22` (probado con 22.22.2) y npm 10+

## Ejecutar (dos terminales abiertas durante toda la práctica)
```bash
# Terminal 1 — backend en http://localhost:8080 (opción recomendada y reproducible)
cd backend
mvn clean package
java -jar target/clientes-backend-1.0.0.jar

# Alternativa directa (nota: en Windows, mvn spring-boot:run puede fallar si la ruta contiene acentos/espacios):
# cd backend && mvn spring-boot:run

# Terminal 2 — frontend en http://localhost:5173
cd frontend && npm install && npm run dev
```
Verificación rápida del backend:
```bash
curl -i http://localhost:8080/api/clientes                 # 200 con 5 clientes
curl -i "http://localhost:8080/api/clientes?scenario=empty" # 200 con []
curl -i "http://localhost:8080/api/clientes?scenario=error" # 503
```

## Restaurar el starter
El estado inicial está marcado con el tag Git `starter-3`.
```bash
git reset --hard starter-3 && git clean -fd   # clean respeta .gitignore (node_modules se conserva)
```
La solución no existe en este árbol.


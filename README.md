# Proyecto Integrador II - Digital Alpha

Este repositorio contiene el proyecto digital alpha que se encargará de brindar los datos a digital money, una plataforma de transferencia de dinero.

![Imagen de referencia](https://res.cloudinary.com/sagarciaescobar/image/upload/v1666376929/Landing_page.jpg)

### Arquitectura de la aplicacion

La arquitectura de la aplicacion consiste en:

- gateway service
- configuration service
- discovery service
- accountDTO service

## How to run

```shell
git clone https://gitlab.ctd.academy/ctd/proyecto-integrador-2/proyecto-integrador-1022/0321-ft-c2-back/grupo-01.git && cd grupo-01
mvn clean package -DskipTests && docker-compose up --build
```

# EndPoints

#### User service
Contiene la logica para la consulta y modifcacion de las cuentas

| Method	 | Path	      | Description	                         | User authenticated	 |
|---------|------------|--------------------------------------|:-------------------:|
| POST	   | /users	    | Crea un usuario con su cuenta	       |                     |
| GET	    | /users/{id}	 | Retorna los datos de un usuario      |          ×          |
| DELETE	 | /users/{id}	 | Inactiva un usuario	                 |              ×         |
| POST	   | /users/logout	 | Cierra todas las sesiones	           |          ×          |
| PATCH	  | /users	    | Modifcar la informacion del usuario	 |         ×             |

#### Account service
Contiene la logica para la consulta y modifcacion de las cuentas

| Method	 | Path	                | Description	                                                    | User authenticated	 | Available from UI |
|---------|----------------------|-----------------------------------------------------------------|:-------------------:|:-----------------:|
| GET	    | /accounts/{accountDTO}	 | Get specified accountDTO data	                                     |                     |                   |
| GET	    | /accounts/current	   | Get current accountDTO data	                                       |          ×          |         ×         |
| GET	    | /accounts/demo	      | Get demo accountDTO data (pre-filled incomes/expenses items, etc)	 |                     |        	×         |
| PUT	    | /accounts/current	   | Save current accountDTO data	                                      |          ×          |         ×         |
| POST	   | /accounts/	          | Register new accountDTO	                                           |                     |         ×         |

#### Transaction service
Contiene la logica para la consulta y modifcacion de las cuentas

| Method	 | Path	                   | Description	                                                       | User authenticated	 | Available from UI |
|---------|-------------------------|--------------------------------------------------------------------|:-------------------:|:-----------------:|
| GET	    | /accounts/{accountDTO}	 | Get specified accountDTO data	                                     |                     |                   |
| GET	    | /accounts/current	      | Get current accountDTO data	                                       |          ×          |         ×         |
| GET	    | /accounts/demo	         | Get demo accountDTO data (pre-filled incomes/expenses items, etc)	 |                     |        	×         |
| PUT	    | /accounts/current	      | Save current accountDTO data	                                      |          ×          |         ×         |
| POST	   | /accounts/	             | Register new accountDTO	                                           |                     |         ×         |
| POST	   | /accounts/deposit	      | Create a deposit	                                                  |         ×         |         ×         |

#### Transaction service
Contiene la logica para la consulta y modifcacion de las cuentas

| Method	 | Path	                | Description	                                                    | User authenticated	 | Available from UI |
|---------|----------------------|-----------------------------------------------------------------|:-------------------:|:-----------------:|
| GET	    | /accounts/{accountDTO}	 | Get specified accountDTO data	                                     |                     |                   |
| GET	    | /accounts/current	   | Get current accountDTO data	                                       |          ×          |         ×         |
| GET	    | /accounts/demo	      | Get demo accountDTO data (pre-filled incomes/expenses items, etc)	 |                     |        	×         |
| PUT	    | /accounts/current	   | Save current accountDTO data	                                      |          ×          |         ×         |
| POST	   | /accounts/	          | Register new accountDTO	                                           |                     |         ×         |


#### Cards service
Contiene la logica para la creación, consulta y modifcacion de las tarjetas asociadas

| Method	 | Path	                      | Description	                                 | User authenticated	 | Available from UI |
|---------|----------------------------|----------------------------------------------|:-------------------:|:-----------------:|
| GET	    | /cards/getAll/{accountId}	 | Get all cards from specific account	         |          ×          |         ×         |
| GET	    | /cards/getCard/{cardId}	   | Get a card using the id                      |                     |                   |
| POST	   | /cards	                    | Create a new card                            |          ×          |        	×         |
| DELETE	 | /cards/{cardId}	                    | Delete a card by ID	 |          ×          |         ×         |

Accede a http://localhost:8080/api/test
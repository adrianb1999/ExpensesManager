# Expenses Manager

## About
This project represent a simple "Expense Manager" web application in
which you create an account and add your expenses.

## Documentation

### Create account example:
`# POST /api/createAccount`
```
Request Header:
 Content-Type: application/json
```
`Request Body`
```json
 {
   "username":"adi",   
   "email":"email@example.ex",
   "password":"pass"
 }
```

### Login example:  

`# POST /login`
```
Request Header:
 Content-Type: application/json
```
`Request Body:`
```json
{
  "username":"adi",   
  "password":"pass"
}
```
```
Response Header:
 Authorization : <JWTToken>
 Set-Cookie: Authorization= <JWTToken>
```

### Add expense example
` #POST /api/expense`  

```
Request Header:
 Content-Type: "application/json"
 
 Authorization: <JWTToken>
 OR
 Cookie: <JWTToken>
```
`Request Body`
```json
{
 "title":"Title",
 "category":"FUEL",
 "payMethod":"CASH",
 "date":"2022-02-02",
 "amount":"123.45",
 "details":"Details"
}
```

## Screenshots  

Login page  
Expenses page  
Statistics page  
User info page

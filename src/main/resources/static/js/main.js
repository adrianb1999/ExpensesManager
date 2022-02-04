function onLoadCustom() {
    getExpenses()
    getLastSevenDays()
}

function login() {
    let name = document.getElementById("username").value;
    let password = document.getElementById("password").value;
    console.log("LOGING: " + name + " password = " + password)
    fetch('/login',
        {
            method: 'POST',
            body: JSON.stringify({'username': name, 'password': password}),
            credentials: 'include'
        })
        .then(response => {

            console.log(...response.headers);

            if (response.status == 200)
                window.location.href = "user.html"
            else if(response.status === 400){
                response.json().then(data =>
                    document.getElementById("loginError").textContent = data.message
                )
            }else if(response.status === 404){
                document.getElementById("loginError").textContent = "Cannot connect to the server!";
            }
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function getExpenses() {
    fetch('/api/users/expenses',
        {
            method: 'GET',
        })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);

            let table = document.getElementById("tableId");

            table.innerHTML = "";

            for (let i of data) {
                table.insertAdjacentHTML("beforeend",
                    `<tr> 
                                            <td>${i.date}</td>
                                            <td>${i.category}</td>
                                            <td>${i.payMethod}</td>
                                            <td>${i.amount}</td>
                                            <td>${i.details}</td>
                                            <td><button class="changeButton">Change</button></td>
                                            <td><button class="deleteButton" onClick="deleteExpense(${i.id})">Delete</button></td>
                                        </tr>`
                );
            }
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function createUser() {
    let name = document.getElementById("createUsername").value;
    let password = document.getElementById("createPassword").value;
    let email = document.getElementById("createEmail").value;
    console.log(name + " " + password)

    fetch('/api/createUser',
        {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({'username': name, 'password': password, 'email': email}),
        })
        .then(response => {
            if (response.status == 200)
                window.location.href = "login.html";
            else {
                let errorLaber = document.getElementById("loginError");
            }
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function postExpense() {
    let category = document.getElementById("categories").value
    let payMethod = document.getElementById("payMethods").value
    let amount = document.getElementById("amount").value
    let details = document.getElementById("details").value
    let date = document.getElementById("date").value


    console.log(category + " " + payMethod + " " + amount + " " + details + " " + date)

    fetch('/api/users/expenses',
        {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                'category': category,
                'payMethod': payMethod,
                'amount': amount,
                'details': details,
                'date': date
            }),
        }).then(response =>{
            location.reload()
        })
        .catch((error) => {
            console.error('Error:', error)
        });
}
function getLastSevenDays(){
    let table = document.getElementById("lastSevenDays");
    fetch('/api/lastWeekTotalPerDays',
        {
            method: 'GET',
        })
        .then(response => response.json())
        .then(data => {
            console.log('Success:', data);

            table.innerHTML = "";

            for (let i of data) {
                table.insertAdjacentHTML("beforeend",
                                    `<tr> 
                                            <td>${i.date}</td>  
                                            <td>${i.total}</td>
                                         </tr>`
                );
            }
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function deleteExpense(expenseId) {
    fetch(`/api/users/expenses/${expenseId}`,
        {
            method: 'DELETE',
            headers: {
            },
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}
function resetPassword() {
    const urlParams = new URLSearchParams(window.location.search);
    const confirmToken = urlParams.get('token')

    let password = document.getElementById("passwordReset").value

    console.log(confirmToken)
    console.log(password)

    fetch(`/api/passwordReset?token=${confirmToken}`,{
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            'password' : password
        })
    }).then(response =>{
        if(response.status === 200)
            window.location.href = "login.html";
    }).catch((error) => {
        console.error('Error:', error);
    });
}

function sendResetPasswordLink(){

    let message = document.getElementById("linkSend")
    let username = document.getElementById("usernameField").value

    fetch('/api/passwordResetSendLink',{
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            'username' : username
        })
    }).then(response =>{
        if(response.status === 200)
            message.textContent = "Link send to email!";
    }).catch((error) => {
        console.error('Error:', error);
    });
}


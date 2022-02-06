function onLoadCustom() {
    getExpenses()
    //getLastSevenDays()
}

function login() {
    let name = document.getElementById("username").value;
    let password = document.getElementById("password").value;

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
            else if (response.status === 400) {
                response.json().then(data =>
                    document.getElementById("loginError").textContent = data.message
                )
            } else if (response.status === 404) {
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
            allExpenses = data
            getExpensesPage(0, 10)
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

var allExpenses
var pageNum = 0
var globalPageSize = 10

function getExpensesPage(page, pageSize) {

    let table = document.getElementById("tableId");

    table.innerHTML = ' <thead><tr>\n' +
        '                <th>Title</th>\n' +
        '                <th>Date</th>\n' +
        '                <th>Category</th>\n' +
        '                <th>Amount</th>\n' +
        '                <th>Pay method</th>\n' +
        '                <th>Details</th>\n' +
        '             </tr></thead>';
    if (page == 1)
        pageNum++
    else if (page == -1)
        pageNum--

    if (pageSize == null)
        pageSize = globalPageSize
    else
        globalPageSize = pageSize

    let maxPage = Math.ceil(allExpenses.length / pageSize)

    if (pageNum < 0)
        pageNum = 0

    if (pageNum >= maxPage)
        pageNum = maxPage - 1;

    console.log("Global = " + globalPageSize + " Local = " + pageSize)

    let firstElement = pageNum * pageSize
    let lastElement = +firstElement + +pageSize

    lastElement = Math.min(lastElement, allExpenses.length)

    console.log("First element= " + firstElement + " Last element= " + lastElement + " Total size= " + allExpenses.length + " Last Page=" + maxPage)
    console.log("Current page= " + pageNum)

    for (let i = firstElement; i < lastElement; i++) {
        table.insertAdjacentHTML("beforeend",
            `<tr> 
                                            <td>${allExpenses[i].title}</td>
                                            <td>${allExpenses[i].date}</td>
                                            <td>${allExpenses[i].category}</td>
                                            <td>${allExpenses[i].amount}</td>
                                            <td>${allExpenses[i].payMethod}</td>
                                            <td>${allExpenses[i].details}</td>
<!--                                            <td><button class="changeButton">Change</button></td>-->
<!--                                            <td><button class="deleteButton" onClick="deleteExpense(${allExpenses[i].id})">Delete</button></td>-->
                                        </tr>`)
    }
    table.insertAdjacentHTML("beforeend", '<tfoot><tr> '+
        '                <td><button onclick="getExpensesPage(-1, null)" class="pageButton"><--</button></td>\n'+
        '                <td colspan="3"><p id="currentPage"></p></td>\n' +
        '                <td><button onclick="getExpensesPage(1, null)" class="pageButton">--></button></td>\n' +
        '<td> '+
             '                   <select id="expensesPerPage" onchange="getExpensesPage(0, value)" >\n' +
                 '                       <option selected>Select size</option>\n' +
                 '                       <option value="10">10</option>\n' +
                 '                       <option value="20">20</option>\n' +
                 '                       <option value="30">30</option>\n' +
                 '                       <option value="40">40</option>\n' +
                 '                       <option value="50">50</option>\n' +
                 '                   </select>\n' +
             '                </td>\n'+
        '                 </tr> </tfoot>');

    document.getElementById("currentPage").textContent = (pageNum + 1);
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
                response.json().then(data => {
                    document.getElementById("createUserMessage").textContent = data.message;
                })

            }
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function postExpense() {
    let title = document.getElementById("title").value
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
                'title': title,
                'category': category,
                'payMethod': payMethod,
                'amount': amount,
                'details': details,
                'date': date
            }),
        }).then(response => {
        location.reload()
    })
        .catch((error) => {
            console.error('Error:', error)
        });
}

function getLastSevenDays() {
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
            headers: {},
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

    fetch(`/api/passwordReset?token=${confirmToken}`, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            'password': password
        })
    }).then(response => {
        if (response.status === 200)
            window.location.href = "login.html";
    }).catch((error) => {
        console.error('Error:', error);
    });
}

function sendResetPasswordLink() {

    let message = document.getElementById("linkSend")
    let username = document.getElementById("usernameField").value

    fetch('/api/passwordResetSendLink', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            'username': username
        })
    }).then(response => {
        if (response.status === 200)
            message.textContent = "Link send to email!";
        else {
            response.json().then(data =>
                message.textContent = data.message
            )
        }
    }).catch((error) => {
        console.error('Error:', error);
    });
}


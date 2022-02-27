function escapeHtml(unsafe) {
    return unsafe
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
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

            if (response.status === 200)
                window.location.href = "user.html"
            else if (response.status === 400) {
                response.json().then(data =>
                    showAlert(data.message)
                )

            } else if (response.status === 404) {
                showAlert("Cannot connect to the server!")
            }
        })
        .catch((error) => {
            showAlert("Cannot connect to the server!")
            console.error('Error:', error);
        });
}

function amountSwitch() {
    let switchValue = document.getElementById("amountCategories")
    if (switchValue.value === "fixed") {
        document.getElementById("amountFilter").style.display = "block";
        document.getElementById("amountMinFilter").style.display = "none";
        document.getElementById("amountMaxFilter").style.display = "none";

        document.getElementById("amountMinFilter").value = "";
        document.getElementById("amountMaxFilter").value = "";

    } else {
        document.getElementById("amountFilter").style.display = "none";
        document.getElementById("amountMinFilter").style.display = "block";
        document.getElementById("amountMaxFilter").style.display = "block";

        document.getElementById("amountFilter").value = "";
    }
}

function dateSwitch() {
    let switchValue = document.getElementById("dateCategories")
    if (switchValue.value === "fixed") {
        document.getElementById("dateFilter").style.display = "block";
        document.getElementById("firstDateFilter").style.display = "none";
        document.getElementById("secondDateFilter").style.display = "none";

        document.getElementById("firstDateFilter").value = "";
        document.getElementById("secondDateFilter").value = "";
    } else {
        document.getElementById("dateFilter").style.display = "none";
        document.getElementById("firstDateFilter").style.display = "block";
        document.getElementById("secondDateFilter").style.display = "block";

        document.getElementById("dateFilter").value = "";
    }
}

function logout() {
    Swal.fire({
        title: 'Wait!',
        text: "Are you sure?",
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, logout!'
    }).then((result) => {
        if (result.isConfirmed) {
            logoutUser()
        }
    })
}

function logoutUser() {
    deleteAllCookies()
    window.location.href = "login.html"
}

//stolen
function deleteAllCookies() {
    let cookies = document.cookie.split(";");

    for (let i = 0; i < cookies.length; i++) {
        let cookie = cookies[i];
        let eqPos = cookie.indexOf("=");
        let name = eqPos > -1 ? cookie.substr(0, eqPos) : cookie;
        document.cookie = name + "=;expires=Thu, 01 Jan 1970 00:00:00 GMT";
    }
}

var allExpenses
var pageNum = 0
var globalPageSize = 30
var globalExpensesLength

var lastSevenDays
var lastSixMonths

var amountSortType = null
var dateSortType = null
var checkType = true

var requestExpenseType = "POST"
var currentExpenseId = null

var deleteList = []
var categoryList = []
var payMethodList = []

var minAmount = null
var maxAmount = null
var amount = null

var firstDate = null
var secondDate = null
var date = null

var passwordMatch = false
var usernameMatch = false
var emailMatch = false

let myChart
let colorPalette = [
    'rgb(255, 99, 132)',
    'rgb(54, 162, 235)',
    'rgb(255, 205, 86)',
    'rgb(46, 204, 113)',
    'rgb(234, 32, 39)',
    'rgb(153, 128, 250)',
    'rgb(255, 165, 2)'
]

let categoriesList = ['FUEL',
    'FOOD',
    'DRINKS',
    'SCHOOL',
    'BEAUTY',
    'CULTURE',
    'HEALTH',
    'GIFT',
    'CLOTHES',
    'TRANSPORT',
    'EDUCATION',
    'OTHER']

function sortTypes(amount, date) {
    if (amount === null && date === null)
        return ''
    if (amount != null)
        return amount
    if (date != null)
        return date
}

function sortCategory(amount, date) {
    if (amount === null && date === null)
        return ''
    if (amount != null)
        return 'AMOUNT'
    if (date != null)
        return 'DATE'
}

function onChangeAmount() {
    amount =  document.getElementById("amountFilter").value
}

function onChangeMinAmount() {
    minAmount = document.getElementById("amountMinFilter").value
}

function onChangeMaxAmount() {
    maxAmount = document.getElementById("amountMaxFilter").value
}

function onChangeDate() {
    date = document.getElementById("dateFilter").value
}

function onChangeFirstDate() {
    firstDate = document.getElementById("firstDateFilter").value
}

function onChangeSecondDate() {
    secondDate = document.getElementById("secondDateFilter").value
}

function getExpensesPage(page, pageSize) {

    if (page === 1)
        pageNum++
    else if (page === -1)
        pageNum--

    if (pageSize == null)
        pageSize = globalPageSize
    else
        globalPageSize = pageSize


    let maxPage
    if (globalExpensesLength != null)
        maxPage = Math.ceil(globalExpensesLength / pageSize)

    if (maxPage != null)
        if (pageNum >= maxPage)
            pageNum = maxPage - 1;

    if (pageNum < 0)
        pageNum = 0

    let payMeth = []
    if (payMethodList.length !== 2)
        payMeth = payMethodList

    fetch('/api/users/expenses?' + new URLSearchParams({
        pageSize: pageSize,
        pageNum: pageNum,
        sortBy: sortCategory(amountSortType, dateSortType),
        sortType: sortTypes(amountSortType, dateSortType),
        category: categoryList,
        payMethod: payMeth,
        amountGreaterThan: minAmount ? minAmount : "",
        amountLessThan: maxAmount ? maxAmount : "",
        amount: amount ? amount : "",
        date: date ? date : "",
        dateAfter: firstDate ? firstDate : "",
        dateBefore: secondDate ? secondDate : "",
    }),
        {
            method: 'GET',
        })
        .then(response => response.json())
        .then(data => {
            allExpenses = data
            let table = document.querySelector("#expensesTable tbody");

            let secondEntity = Math.min((pageSize * (pageNum + 1)), allExpenses.size)
            globalExpensesLength = allExpenses.size

            document.querySelector("#expensesSize").textContent = "Showing " + (pageSize * pageNum + 1) + " to " + secondEntity + " of " + allExpenses.size + " of entries"
            table.innerHTML = '';
            if (allExpenses.size === 0) {
                document.querySelector("#expensesSize").textContent = ""
                table.insertAdjacentHTML("beforeend",
                    `<tr><td>No data...</td></tr>`)
            } else
                for (let i of allExpenses.expenses) {
                    table.insertAdjacentHTML("beforeend",
                        `<tr>   
                                            <td><input type="checkbox" class="checkboxExpense" onclick="addExpenseToDeleteList('${i.id}')" value="${i.id}"></td>
                                            <td>${escapeHtml(i.date)}</td>
                                            <td>${escapeHtml(i.title)}</td>                        
                                            <td>${capitalizeFirstLetter(i.category)}</td>
                                            <td>${i.amount}</td>
                                            <td>${capitalizeFirstLetter(i.payMethod)}</td>
                                            <td>${escapeHtml(i.details)}</td>
                                            <td>
                                                <button class="changeButton" onClick="editExpense(${i.id})">
                                                        <span class="material-icons">
                                                            edit
                                                        </span>
                                                </button>
                                                <button class="deleteButton" onClick="deleteExpense(${i.id})"> 
                                                        <span class="material-icons">
                                                            clear
                                                        </span>                                               
                                                </button>
                                            </td>
                                        </tr>`)
                }
            document.getElementById("currentPage").textContent = (pageNum + 1);
            getLastSevenDays()
            getLastSixMonths()
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function createUser() {
    let name = document.getElementById("createUsername").value;
    let password = document.getElementById("passwordInput").value;
    let email = document.getElementById("createEmail").value;

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
            if (response.status === 200)
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
        if (response.status === 200) {
            showAlert('Expense has been added', 'success', 'Success!')
            getExpensesPage(0, 20)
            closeModal()
            resetForm()
        } else {
            response.json().then(data => {
                showAlert(data.message, 'error', 'Something is wrong!')
            })
        }
    })
        .catch((error) => {
            console.error('Error:', error)
        });
}

function putExpense() {
    let title = document.getElementById("title").value
    let category = document.getElementById("categories").value
    let payMethod = document.getElementById("payMethods").value
    let amount = document.getElementById("amount").value
    let details = document.getElementById("details").value
    let date = document.getElementById("date").value

    fetch(`/api/users/expenses/${currentExpenseId}`,
        {
            method: 'PUT',
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
        if (response.status === 200) {
            showAlert('Expense has been edited', 'success', 'Success!')
            getExpensesPage(0, 20)
            closeModal()
            resetForm()
        } else {
            response.json().then(data => {
                showAlert(data.message, 'error', 'Something is wrong!')
            })
        }
    })
        .catch((error) => {
            console.error('Error:', error)
        });
}

function deleteExpenseList() {
    fetch(`/api/users/expenses?` + new URLSearchParams({
        expenseIds: deleteList
    }),
        {
            method: 'DELETE',
            headers: {},
        }).then(response => {
        if (response.status === 200) {
            showAlert('The expenses has been deleted.', 'success', 'All deleted!')
            deleteList.splice(0, (deleteList.length))
            checkType = true
            getExpensesPage(0, 20)
        }
    })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function addPayMethodToList(payMethod) {

    if (payMethodList.length === 0) {
        payMethodList.push(payMethod)
        document.getElementById(payMethod).value = "check"
        return
    }

    for (let i = 0; i < payMethodList.length; i++) {
        if (payMethodList[i] === payMethod) {
            payMethodList.splice(i, 1)
            document.getElementById(payMethod).value = ""
            return
        }
    }
    payMethodList.push(payMethod)
    document.getElementById(payMethod).value = "check"
}

function addCategoryToList(category) {

    if (categoryList.length === 0) {
        categoryList.push(category)
        document.getElementById(category).value = "check"
        return
    }
    for (let i = 0; i < categoryList.length; i++) {
        if (categoryList[i] === category) {
            categoryList.splice(i, 1)
            document.getElementById(category).value = ""
            return
        }
    }
    categoryList.push(category)
    document.getElementById(category).value = "check"
}

function addExpenseToDeleteList(expense) {
    let counter = 0

    if (deleteList.length === 0) {
        deleteList.push(expense)
        document.getElementById("deleteSelectedExpenses").disabled = false
        return
    }
    for (let i = 0; i < deleteList.length; i++) {
        if (deleteList[i] === expense) {
            deleteList.splice(i, 1)
            counter++
        }
    }
    if (counter === 0) {
        deleteList.push(expense)
        document.getElementById("deleteSelectedExpenses").disabled = false
    }
    document.getElementById("deleteSelectedExpenses").disabled = deleteList.length === 0;
}

function getLastSevenDays() {
    fetch('/api/users/statistics/lastWeekTotalPerDays',
        {
            method: 'GET',
        })
        .then(response => response.json())
        .then(data => {
            lastSevenDays = data
            showChart()
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function getLastSixMonths() {
    fetch('/api/users/statistics/lastMonthsTotalPerMonthByCategory/6',
        {
            method: 'GET',
        })
        .then(response => response.json())
        .then(data => {
            lastSixMonths = data
            showMonthCart()
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function getTotalSpent() {
    fetch('/api/users/statistics/totalSpent',
        {
            method: 'GET',
        })
        .then(response => response.json())
        .then(data => {
            document.getElementById("totalSpentText").innerHTML = data.total
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function getDayAverage() {
    fetch('/api/users/statistics/dayAverage',
        {
            method: 'GET',
        })
        .then(response => response.json())
        .then(data => {
            document.getElementById("dayAverageText").innerHTML = data.average
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function getMonthAverage() {
    fetch('/api/users/statistics/monthAverage',
        {
            method: 'GET',
        })
        .then(response => response.json())
        .then(data => {
            document.getElementById("monthAverageText").innerHTML = data.average
        })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function deleteExpenses() {
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: deleteList.length === 1 ? 'Yes, delete it!' : 'Yes, delete all!'
    }).then((result) => {
        if (result.isConfirmed) {
            deleteExpenseList()
        }
    })
}

function deleteExpense(expenseId) {
    Swal.fire({
        title: 'Are you sure?',
        text: "You won't be able to revert this!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Yes, delete it!'
    }).then((result) => {
        if (result.isConfirmed) {
            delExpense(expenseId)
        }
    })
}

function delExpense(expenseId) {
    fetch(`/api/users/expenses/${expenseId}`,
        {
            method: 'DELETE',
            headers: {},
        }).then(response => {
        if (response.status === 200) {
            showAlert('The expense has been deleted.', 'success', 'Deleted!')
            getExpensesPage(0, 20)
        }
    })
        .catch((error) => {
            console.error('Error:', error);
        });
}

function resetPassword() {
    const urlParams = new URLSearchParams(window.location.search);
    const confirmToken = urlParams.get('token')

    let password = document.getElementById("passwordReset").value

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

function applyFilters() {
    pageNum = 0
    getExpensesPage(0, 20)
    document.getElementById("filterModal").style.display = "none";
}

function sendResetPasswordLink() {
    let email = document.getElementById("usernameField").value

    fetch('/api/passwordResetSendLink', {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            'email': email
        })
    }).then(response => {
        if (response.status === 200) {
            showAlert("Email sent!", 'success', 'Success!')
        } else {
            response.json().then(data =>
                showAlert(data.message)
            )
        }
    }).catch((error) => {
        console.error('Error:', error);
    });
}

document.addEventListener("DOMContentLoaded", function () {

    if (document.body.classList.contains("resetPasswordFrom")) {
        document.querySelector("#newPassword-form").onsubmit = function () {
            resetPassword()
            return false
        }
    }

    if (document.body.classList.contains("passwordReset")) {
        document.querySelector("#resetPassword-form").onsubmit = function () {
            sendResetPasswordLink()
            return false
        }
    }

    if (document.body.classList.contains("login")) {
        document.querySelector("#login-form").onsubmit = function () {
            login()
            return false
        }
    }
    if (document.body.classList.contains("createUser")) {
        document.querySelector("#createAccountForm").onsubmit = function () {
            if (passwordMatch)
                createUser()

            return false
        }
    }
    if (document.body.classList.contains("user")) {

        document.getElementById("amountFilter").style.display = "block";
        document.getElementById("amountMinFilter").style.display = "none";
        document.getElementById("amountMaxFilter").style.display = "none";

        document.getElementById("dateFilter").style.display = "block";
        document.getElementById("firstDateFilter").style.display = "none";
        document.getElementById("secondDateFilter").style.display = "none";

        document.querySelectorAll(".btn-toggle-panel").forEach(function (el) {
            el.onclick = function (e) {
                e.preventDefault()
                let target_el = el.dataset.target

                let active_panel = document.querySelector(".panels .panel.active")
                if (active_panel) {
                    active_panel.classList.remove("active")
                }

                document.querySelector(`.panels .panel${target_el}`).classList.add("active")
            }
        })

        getExpensesPage(0, 20)
        getDayAverage()
        getMonthAverage()
        getTotalSpent()

        document.querySelector("#logout").onclick = function () {
            logout()
        }

        document.querySelector("#updateUsernameForm").onsubmit = function () {
            if (usernameMatch)
                changeUserInfo('username')
            return false
        }
        document.querySelector("#updateEmailForm").onsubmit = function () {
            if (emailMatch)
                changeUserInfo('email')
            return false
        }
        document.querySelector("#updatePasswordForm").onsubmit = function () {
            if (passwordMatch)
                changeUserInfo('password')
            return false
        }
        document.querySelector("#expenseForm").onsubmit = function () {
            if (requestExpenseType === "POST")
                postExpense()
            else if (requestExpenseType === "PUT") {
                putExpense()
            }
            return false
        }

        var modal = document.getElementById("myModal");
        var filterModal = document.getElementById("filterModal");

        var span = document.getElementsByClassName("close")[0];
        var span2 = document.getElementsByClassName("close")[1];

        span2.onclick = function () {
            filterModal.style.display = "none";
        }

        span.onclick = function () {
            resetForm()
            modal.style.display = "none";
        }

        window.onclick = function (event) {
            if (event.target === modal || event.target === filterModal) {
                modal.style.display = "none";
                filterModal.style.display = "none";
                resetForm()
            }
        }

        let amountSorting = document.getElementById("sortAmount")
        amountSorting.onclick = function () {

            if (amountSortType == null)
                amountSortType = 'ASC'
            else if (amountSortType === 'ASC')
                amountSortType = 'DESC'
            else if (amountSortType === 'DESC')
                amountSortType = 'ASC'
            dateSortType = null
            getExpensesPage(0, 20)
        }
        let dateSorting = document.getElementById("sortDate")
        dateSorting.onclick = function () {
            if (dateSortType == null)
                dateSortType = 'ASC'
            else if (dateSortType === 'ASC')
                dateSortType = 'DESC'
            else if (dateSortType === 'DESC')
                dateSortType = 'ASC'

            amountSortType = null
            getExpensesPage(0, 20)
        }
        let selectingBox = document.getElementById("selectBox")
        selectingBox.onclick = function () {

            checkType = !checkType

            let allBoxes = document.getElementsByClassName("checkboxExpense")
            for (i of allBoxes) {
                if (checkType === false) {
                    if (i.checked === false) {
                        addExpenseToDeleteList(i.value)
                        i.checked = true
                    }
                } else if (i.checked === true) {
                    i.checked = false
                    addExpenseToDeleteList(i.value)
                }
            }
        }
    }
});

function openFilterModal() {
    document.getElementById("filterModal").style.display = "flex";
}

function openExpenseModal() {
    document.getElementById("myModal").style.display = "block"
}

function closeModal() {
    document.getElementById("myModal").style.display = "none"
}

function resetForm() {

    requestExpenseType = "POST"
    currentExpenseId = null

    document.getElementById("title").value = ""
    document.getElementById("categories").value = ""
    document.getElementById("payMethods").value = ""
    document.getElementById("amount").value = ""
    document.getElementById("details").value = ""
    document.getElementById("date").value = ""

    document.getElementById("addExpenseButton").textContent = "Add expense"
    document.getElementById("expenseFormTitle").textContent = "Add expense"
}

function editExpense(expenseId) {

    for (let i = 0; i < allExpenses.expenses.length; i++)
        if (allExpenses.expenses[i].id === expenseId) {
            document.getElementById("title").value = allExpenses.expenses[i].title
            document.getElementById("categories").value = allExpenses.expenses[i].category
            document.getElementById("payMethods").value = allExpenses.expenses[i].payMethod
            document.getElementById("amount").value = allExpenses.expenses[i].amount
            document.getElementById("details").value = allExpenses.expenses[i].details
            document.getElementById("date").value = allExpenses.expenses[i].date
        }

    document.getElementById("myModal").style.display = "block";

    document.getElementById("addExpenseButton").textContent = "Edit expense"
    document.getElementById("expenseFormTitle").textContent = "Edit expense"

    requestExpenseType = "PUT"

    currentExpenseId = expenseId
}

function showAlert(message, type = 'error', title = 'Error', timer = 2000) {
    Swal.fire({
        icon: type,
        title: title,
        text: message,
        timer: timer,
    })
}

function setMatchText(match, message, textMessageId) {
    if (match && message !== '') {
        document.getElementById(textMessageId).style.color = 'green';
        document.getElementById(textMessageId).innerHTML = message + ' match!';
    } else if (!match && message !== '') {
        document.getElementById(textMessageId).style.color = 'red';
        document.getElementById(textMessageId).innerHTML = message + ' doens\'t match!';
    } else {
        document.getElementById(textMessageId).innerHTML = '';
    }
}

function matchChecking(firstId, secondId, textMessageId) {

    let match = document.getElementById(firstId).value === document.getElementById(secondId).value
    let currentForm

    if (firstId === 'userInput') {
        usernameMatch = match
        currentForm = 'Usernames'
    } else if (firstId === 'emailInput') {
        emailMatch = match
        currentForm = 'Emails'
    } else if (firstId === 'passwordInput') {
        passwordMatch = match
        currentForm = 'Passwords'
    }

    if (document.getElementById(firstId).value === '' || document.getElementById(secondId).value === '') {
        setMatchText(match, '', textMessageId)
    } else {
        setMatchText(match, currentForm, textMessageId)
    }
}

function changeUserInfo(inputType) {
    let username = document.querySelector("#userInput").value
    let email = document.querySelector("#emailInput").value
    let newPassword = document.querySelector("#passwordInput").value

    if (username == null) return;
    let cleanForm
    let data = {}
    if (inputType === "username") {
        data.username = username
        data.password = document.querySelector("#passwordInputUsername").value
        cleanForm = cleanUsernameForm
    } else if (inputType === "email") {
        data.email = email
        data.password = document.querySelector("#passwordInputEmail").value
        cleanForm = cleanEmailForm
    } else if (inputType === "password") {
        data.newPassword = newPassword
        data.password = document.querySelector("#oldPasswordInput").value
        cleanForm = cleanPasswordForm
    }

    fetch('/api/users/updateInfo',
        {
            method: 'PUT',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(data),
        }).then(response => {
        if (response.status === 200) {
            showAlert('User information updated!', 'success', 'Success!')
            cleanForm()
            if (inputType === "username")
                logoutUser();
        } else {
            response.json().then(data => {
                showAlert(data.message)
            })
        }
    }).catch((error) => {
        console.error('Error:', error)
    });
}

function cleanUsernameForm() {
    document.getElementById("userInput").value = ""
    document.getElementById("confirmUserInput").value = ""
    document.getElementById("passwordInputUsername").value = ""
    document.getElementById("usernameWordMessage").innerHTML = ""
}

function cleanEmailForm() {
    document.getElementById("emailInput").value = ""
    document.getElementById("confirmEmailInput").value = ""
    document.getElementById("passwordInputEmail").value = ""
    document.getElementById("emailWordMessage").innerHTML = ""
}

function cleanPasswordForm() {
    document.getElementById("passwordInput").value = ""
    document.getElementById("confirmPasswordInput").value = ""
    document.getElementById("oldPasswordInput").value = ""
    document.getElementById("passWordMessage").innerHTML = ""
}

function showChart() {

    let days = []
    let totals = []
    for (let i = 0; i < lastSevenDays.length; i++) {
        days[i] = lastSevenDays[i].date
        totals[i] = lastSevenDays[i].total
    }
    let ctx = document.getElementById('myChart').getContext('2d');
    let myChart = new Chart(ctx, {
        type: 'bar',
        data: {
            labels: days,
            datasets: [{
                data: totals,
                fill: false,
                borderColor: 'rgb(0, 0, 0)',
                backgroundColor: colorPalette,
                tension: 0.25
            }]
        },
        options: {
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                x: {
                    stacked: true,
                    ticks: {
                        color: "#FFFFFF"
                    },
                    grid: {
                        color: "#ffffff"
                    }
                },
                y: {
                    stacked: true,
                    ticks: {
                        color: "#FFFFFF"
                    },
                    grid: {
                        color: "#ffffff"
                    }
                }
            }
        }
    });
}

let currentColor = Math.floor(Math.random() * (colorPalette.length - 1))

function getColor() {
    currentColor++
    if (currentColor > colorPalette.length - 1)
        currentColor = 0
    return colorPalette[currentColor]
}

function addLine(label, lineData) {
    myChart.data.datasets.push({
        label: label,
        data: lineData,
        backgroundColor: getColor(),
        fontColor: 'rgb(255, 255, 255)',
        fill: false
    })
    myChart.update()
}

function showMonthCart() {
    let ctx = document.getElementById('last6M').getContext('2d');
    myChart = new Chart(ctx, {
        title: "Last 6 months",
        type: 'bar',
        data: {
            labels: lastSixMonths.map(element => element.month + "-" + element.year),
        },
        options: {
            plugins: {
                legend: {
                    display: false
                }
            },
            scales: {
                x: {
                    stacked: true,
                    ticks: {
                        color: "#FFFFFF"
                    },
                    grid: {
                        color: "#ffffff"
                    }
                },
                y: {
                    stacked: true,
                    ticks: {
                        color: "#FFFFFF"
                    },
                    grid: {
                        color: "#ffffff"
                    }
                }
            }
        }
    });

    categoriesList.forEach(cat =>
        addLine(cat, lastSixMonths.map(x => x.categories[cat])))
}

function capitalizeFirstLetter(string) {
    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
}
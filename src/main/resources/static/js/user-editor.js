let currentUserId = null;
let usersData = null;
const createNewUserForm = document.getElementById('newUserForm');
const editUserForm = document.getElementById('editUserForm');
const newUserFormCloseButton = document.getElementById('newUserFormCloseButton');
const editUserFormCloseButton = document.getElementById('editUserFormCloseButton');
const messageDeleteUserModal = document.getElementById('messageDeleteUserModal')
const detailList = document.getElementById('detailList');
const token = document.head.querySelector('meta[name="_csrf"]').getAttribute('content');
const baseUrl = window.origin
window.onLoad = setUsers();

createNewUserForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const formData = new FormData(createNewUserForm);
    const firstName = formData.get('firstName');
    const lastName = formData.get('lastName');
    const role = formData.get('role');
    const login = formData.get('login');
    const password = formData.get('password');
    addNewUser(firstName, lastName, role, login, password).then();
    createNewUserForm.reset();
});

editUserForm.addEventListener('submit', (event) => {
    event.preventDefault();
    const formData = new FormData(editUserForm);
    const firstName = formData.get('firstName');
    const lastName = formData.get('lastName');
    const role = formData.get('role');
    const login = formData.get('login');
    const password = formData.get('password');
    editUser(currentUserId, firstName, lastName, role, login, password).then();
    createNewUserForm.reset();
})

detailList.addEventListener('click', ({target}) => {
    if (target.closest('.user-info')) {
        clickUserHandler(target);
    }
});

messageDeleteUserModal.addEventListener('click', ({target}) => {
    if (target.closest('.modal-content')) {
        userDeleteClickHandler(target);
    }
})

function clickUserHandler(target) {
    setCurrentUserId(target);
    if (target.classList.contains('users-list-item')) {
        target.closest('.user-info').classList.toggle('open');
    } else if (target.closest('.user__edit-button')) {
        fillEditUserForm(currentUserId).then();
    }
}

function userDeleteClickHandler(target) {
    if (target.closest('.confirm-button')) {
        deleteUser(currentUserId).then();
    }
}

function setCurrentUserId(target) {
    currentUserId = target.closest('.user-info').dataset.id;
}

async function fillEditUserForm(currentUserId) {
    let userInfo = usersData.find(({userId}) => {
        return currentUserId == userId;
    });
    getUserInfoEditFormHtml(currentUserId, userInfo.firstName, userInfo.lastName, userInfo.role, userInfo.login, userInfo.password);
}

function getUserInfoEditFormHtml(userId, firstName, lastName, role, login, password) {
    document.getElementById('firstName').value = firstName;
    document.getElementById('lastName').value = lastName;
    document.getElementById('role').value = role;
    document.getElementById('login').value = login;
    document.getElementById('password').value = password;
}

async function addNewUser(firstName, lastName, role, login, password) {
    newUserFormCloseButton.click();
    const url = new URL(baseUrl + "/admin/add/user");
    let userInfo = {firstName, lastName, role, login, password};
    const response = await fetch(url.toString(), {
        method: 'POST',
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": token
        },
        body: JSON.stringify(userInfo)
    });
    const result = await response.json();
    usersData = result;
    await setNewUsers(usersData);
}

async function editUser(userId, firstName, lastName, role, login, password) {
    editUserFormCloseButton.click();
    const url = new URL(baseUrl + "/admin/user");
    let userInfo = {userId, firstName, lastName, role, login, password};
    const response = await fetch(url.toString(), {
        method: 'PUT',
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": token
        },
        body: JSON.stringify(userInfo)
    });
    const result = await response.json();
    usersData = result;
    await setNewUsers(usersData);
    location.reload();
}

async function deleteUser(userId) {
    const url = new URL(baseUrl + "/admin/user");
    let params = {userId};
    url.search = new URLSearchParams(params).toString();
    const response = await fetch(url.toString(), {
        method: 'DELETE',
        headers: {
            "Content-Type": "application/json",
            "X-CSRF-TOKEN": token
        }
    });
    const result = await response.json();
    usersData = result;
    await setNewUsers(usersData);
    location.reload();
}

async function setNewUsers(usersList) {
    console.log(usersList);
    detailList.classList.add('active');
    detailList.innerHTML = '';
    console.log(usersData);
    Array.from(await usersList).forEach(data => {
        detailList.appendChild(getUserHtml(data))
    });
}

async function setUsers() {
    usersData = getUsersData();
    console.log(usersData);
    detailList.classList.add('active');
    detailList.innerHTML = '';
    console.log(usersData);
    Array.from(await usersData).forEach(data => {
        detailList.appendChild(getUserHtml(data))
    });
}

async function getUsersData() {
    const url = new URL(baseUrl + "/admin/get/user/all");
    const response = await fetch(url.toString());
    usersData = await response.json();
    return usersData;
}

function getUserHtml({userId, firstName, lastName, login}) {
    const user = document.createElement('div');
    user.className = 'user-info mb-3';
    user.dataset.id = userId;
    user.innerHTML = `
    <div class="row">
      <a class="col-md-9 d-flex justify-content-between" data-bs-toggle="collapse" href='#user${userId}' role="button" aria-expanded="false" aria-controls="collapseExample">
        <p class="user-info__name col-md-5">${firstName} ${lastName}</p>
        <p class="user-info__email user__email__left col-md-5">${login}</p>
      </a>
      <div class="col-md-3 test__control text-md-end">
        <button class="user__edit-button" data-bs-target="#editUserModal" data-bs-toggle="modal"><img src="/img/edit-icon.svg" alt="Edit user" class="icon-btn"></button>
        <button class="user__delete-button" data-bs-target="#messageDeleteUserModal" data-bs-toggle="modal"><img src="/img/delete-icon.svg" alt="Delete user" class="icon-btn"></button>
      </div>
    </div>
  `
    return user
}
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Recipe detail</title>
    <link
            rel="stylesheet"
            href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css"
    />

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400&family=Lato:wght@300;400&family=Playfair+Display+SC&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://unpkg.com/cropperjs/dist/cropper.min.css" />
    <link
            href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
            rel="stylesheet"
            integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"
            crossorigin="anonymous"
    />
    <link rel="stylesheet" href="/css/main.css" />

    <style>
        .Dialog {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.5);
            justify-content: center;
            align-items: center;
            z-index: 9999;
        }

        .dialogContent {
            background: #fff;
            padding: 20px;
            border-radius: 5px;
        }

    </style>
    
</head>
<body>
<header>



    <nav class="navbar custom-navbar-bg fixed-top">
        <div class="container-fluid py-2 px-4">
            <a class="navbar-brand" href="/"><div style="width: 100%"><span style="color: #0B5A18; font-size: 24px; font-family: Lato; font-weight: 700; text-transform: uppercase; word-wrap: break-word">Gastrono</span><span style="color: #12B868; font-size: 24px; font-family: Lato; font-weight: 700; text-transform: uppercase; word-wrap: break-word">Mate</span></div></a>
        </div>
    </nav>



    <div style="height: 100px"> . </div>

</header>

<div id="overlay" class="overlay"></div>

<div id="modal" class="myAlert">
    <div id="modalContent" class="myAlertBody">
        <h4>Input error</h4>
        <p class="pt-2" id="modalText"></p>
        <button class="btn btn-danger float-end" onclick="closeModal()">Close</button>
    </div>
</div>

<div id="cropDialog" class="Dialog">
    <div id="dialogContent" class="p-5 dialogContent">
        <!-- Contenuto della finestra modale -->
        <img id="RowImage" style="display: none; max-width: 100%; height: 500px; " alt="" />
        <button class="btn btn-primary mt-2" onclick="submitCrop()">Submit</button>
        <button class="btn btn-danger mt-2" onclick="cancelCrop()">Cancel</button>
    </div>
</div>

<div class="container py-5">
    <div class="row py-5 justify-content-center">
        <div class="col-md-8 py-2">
            <div class="card">
                <div class="card-body">
                    <h2 class="card-title text-center"><c:if test="${user != null}">Edit User</c:if><c:if test="${user == null}">Sign Up</c:if></h2>
                    <!-- Sign Up Form / Edit Form -->
                    <form id="Form" <c:if test="${user == null}">action="/signup"</c:if><c:if test="${user != null}">action="/user/myProfile/edit"</c:if> method="post">
                        <div class="mb-3">
                            <label for="fullName" class="form-label">Full Name</label>
                            <input type="text" class="form-control" name="fullName" id="fullName" placeholder="${user.fullName}" <c:if test="${user != null}">readonly</c:if> pattern="[A-Za-z0-9\s]+" title="Only letters, numbers, and spaces are allowed" />
                        </div>
                        <div class="mb-3">
                            <label for="email" class="form-label">Email</label>
                            <input type="email" class="form-control" name="email" id="email" placeholder="${user.email}" />
                        </div>
                        <div class="mb-3">
                            <label for="username" class="form-label">Username</label>
                            <input type="text" class="form-control" name="username" id="username" title="Only letters, numbers, spaces and '.' '_' '-'  are allowed" placeholder="${user.username}" <c:if test="${user == null}">required</c:if>">
                        </div>
                        <div class="mb-3" id="passwordContainer">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" onkeyup="showConfirmPassword()" class="form-control" name="password" id="password" title="Required length: 8 characters and at least one number, one uppercase letter, and one special character" placeholder="*******"/>
                        </div>

                        <c:if test="${user != null}">
                            <div class="mb-3">
                                <label for="description" class="form-label">Description</label>
                                <textarea  class="form-control" name="description" id="description"  placeholder="${user.description}"></textarea>
                            </div>
                        </c:if>

                        <!-- Address Section -->
                        <div class="mb-3">
                            <label for="city" class="form-label">City</label>
                            <input type="text" class="form-control" name="city" id="city" pattern="[A-Za-z\s]+" title="Only letters and spaces are allowed" placeholder="${user.address.city}" <c:if test="${user == null}">required</c:if>"/>
                            <label for="country" class="form-label">Country</label>
                            <input type="text" class="form-control" name="country" id="country" pattern="[A-Za-z\s]+" title="Only letters and spaces are allowed" placeholder="${user.address.country}" <c:if test="${user == null}">required</c:if>/>
                            <label for="state" class="form-label">State</label>
                            <input type="text" class="form-control" name="state" id="state" pattern="[A-Za-z\s]+" title="Only letters and spaces are allowed" placeholder="${user.address.state}" <c:if test="${user == null}">required</c:if>/>
                        </div>
                        <!-- Date of Birth Section -->
                        <div class="mb-3">
                            <label for="dateOfBirth" class="form-label">Date of Birth</label>
                            <c:choose>
                                <c:when test="${user != null}">
                                    <input type="text" class="form-control" id="dateOfBirth" name="dateOfBirth" placeholder="${user.dateOfBirth.toLocalDate()}" readonly/>
                                </c:when>
                                <c:otherwise>
                                    <input type="date" class="form-control" id="dateOfBirth" name="dateOfBirth" min="1900-01-01" max="2024-01-16" />
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <img class="py-3" id="CroppedImage" style="display: none; max-width: 100%; height: 100px" alt=""  src=""/>
                        <!-- Crop image section -->
                        <label for="uploadRowImage" class="form-label">Profile picture</label>
                        <div class="input-group">
                            <input id="uploadRowImage" type="file" class="form-control" placeholder="Choose a profile picture" aria-label="Choose a profile picture" aria-describedby="button-addon2" accept="image/*" onchange="updateFileName()">
                            <a class="btn btn-outline-danger" type="button" onclick="removeImage()">Remove image</a>
                        </div>
                        <!-- End crop section -->
                        <div class="mt-5 text-end">
                            <a class="btn custom-btn-color" onclick="submitForm()" ><c:if test="${user != null}">Edit User</c:if><c:if test="${user == null}">Sign Up</c:if></a>
                        </div>
                        <input type="hidden" name="croppedImageB64" id="croppedImageB64" value=""/>

                        <c:if test="${user != null}">
                            <input type="hidden" name="id" value="${user.id}"/>
                        </c:if>

                    </form>
                    <!-- End Sign Up Form / Edit Form-->
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="components/footer.txt" %>

<script>
    window.addEventListener('load', () => {

        let message = "${errorMessage}";
        if(message !== "" && message !== null && message !== undefined) {
            displayModal(message);
        }

    });

    function showConfirmPassword() {

        //create a new fileds if it does not exist
        if(document.getElementById('confirmPassword') === null) {

            const passwordContainer = document.getElementById('passwordContainer');

            const newLabel = document.createElement('label');
            newLabel.setAttribute('for', 'confirmPassword');
            newLabel.setAttribute('class', 'form-label');
            newLabel.innerHTML = 'Confirm Password';
            const newInput = document.createElement('input');
            newInput.setAttribute('type', 'password');
            newInput.setAttribute('class', 'form-control');
            newInput.setAttribute('id', 'confirmPassword');

            //append the new label and input to the password container
            passwordContainer.appendChild(newLabel);
            passwordContainer.appendChild(newInput);

        }
    }


    function isPasswordValid(password) {

        <c:if test="${user != null}">

            if (password.trim() === '') {
                return true;
            }
        </c:if>
        // Minimum length of 8 characters
        if (password.length < 8) {
            return false;
        }
        // At least one digit
        if (!/\d/.test(password)) {
            return false;
        }
        // At least one uppercase letter
        if (!/[A-Z]/.test(password)) {
            return false;
        }

        if (!/[._\-@%#!&^{}[\]]/.test(password)) {
            return false;
        }

        // All criteria satisfied
        return true;
    }
    function isAtLeast18YearsOld(dateOfBirth) {

        const today = new Date();
        const birthDate = new Date(dateOfBirth);
        const age = today.getFullYear() - birthDate.getFullYear();
        const monthsDifference = today.getMonth() - birthDate.getMonth();
        const daysDifference = today.getDate() - birthDate.getDate();

        // Check if the person is less than 18 years old
        if (age < 18 || (age === 18 && (monthsDifference < 0 || (monthsDifference === 0 && daysDifference < 0)))) {
            displayModal("You must be at least 18 years old to register");
            return false;
        }

        return true;
    }
    function checkData() {
        const UsernamePattern = /^[A-Za-z0-9\s._-]+$/;

        const email = document.getElementById('email');
        const username = document.getElementById('username');
        const password = document.getElementById('password');
        const state = document.getElementById('state');
        const country = document.getElementById('country');
        const city = document.getElementById('city');

        <c:choose>

        <c:when test="${user == null}">

        const fullName = document.getElementById('fullName');
        if (!fullName.checkValidity()) {
            displayModal("Full name is a required field. Only letters, numbers, and spaces are allowed");
            return false;
        }

        if (!email.checkValidity()) {
            displayModal("Email is a required field");
            return false;
        }

        if (username.value.trim() === '' || !UsernamePattern.test(username.value)) {
            displayModal("Username is a required field. Only letters, numbers, spaces and '.' '_' '-'  are allowed");
            return false;
        }


        if (!isPasswordValid(password.value)) {
            displayModal("Password is a required field. Required length: 8 characters and at least one number, one uppercase letter, and one special character among: ._-@%#!&^{}[]");

            return false;
        }

         confirmPassword = document.getElementById('confirmPassword');

        if (confirmPassword !== null) {
            if (confirmPassword.value !== password.value) {
                displayModal("Passwords do not match");
                return false;
            }
        }


        if (!state.checkValidity()) {
            displayModal("State is a required field. Only letters and spaces are allowed");
            return false;
        }


        if (country.value.trim() !== '') {
            if (!country.checkValidity()) {
                displayModal("Only letters and spaces are allowed in the country field");
                return false;
            }
        }


        if (city.value.trim() !== '') {
            if (!city.checkValidity()) {
                displayModal("Only letters and spaces are allowed in the city field");
                return false;
            }
        }

        const dateOfBirth = document.getElementById('dateOfBirth');
        if (dateOfBirth.value.trim() !== '') {
            return (isAtLeast18YearsOld(dateOfBirth.value));
        } else {
            displayModal("Date of birth is a required field.");
            return false;
        }

        </c:when>

        <c:otherwise>

        if (email.value.trim() !== '' && !email.checkValidity()) {
            displayModal("verify email validity");
            return false;
        }

        if (username.value.trim() !== '' && !UsernamePattern.test(username.value)) {
            displayModal("Only letters, numbers, spaces and '.' '_' '-'  are allowed for username");
            return false;
        }

        if (!isPasswordValid(password.value)) {
            displayModal("Password not allowed. Required length: 8 characters and at least one number, one uppercase letter, and one special character among: ._-@%#!&^{}[]");
            return false;
        }

        confirmPassword = document.getElementById('confirmPassword');

        if (confirmPassword !== null) {
            if (confirmPassword.value !== password.value) {
                displayModal("Passwords do not match");
                return false;
            }
        }

        </c:otherwise>

        </c:choose>

        return true;
    }
    function submitForm() {
        if (checkData()) {
            document.getElementById("Form").submit();
        }
    }
</script>

<!-- External scripts for jQuery, Bootstrap, and custom JavaScript files -->
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script src="https://unpkg.com/cropperjs/dist/cropper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
<script src="/js/Main.js"></script>
<script src="/js/StarScript.js"></script>
</body>
</html>

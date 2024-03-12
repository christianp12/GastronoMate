<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Login</title>
    <!-- External stylesheets for icons and fonts -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css"/>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400&family=Lato:wght@300;400&family=Playfair+Display+SC&display=swap" rel="stylesheet">
    <!-- Stylesheet for image cropping -->
    <link rel="stylesheet" href="https://unpkg.com/cropperjs/dist/cropper.min.css" />
    <!-- Bootstrap stylesheet -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"  crossorigin="anonymous"/>
    <!-- Custom stylesheet -->
    <link rel="stylesheet" href="/css/main.css"/>
</head>
<body>
<!-- Header section with navbar -->
<header>
    <nav class="navbar custom-navbar-bg fixed-top">
        <div class="container-fluid py-2 px-4">
            <a class="navbar-brand" href="/"><div style="width: 100%"><span style="color: #0B5A18; font-size: 24px; font-family: Lato; font-weight: 700; text-transform: uppercase; word-wrap: break-word">Gastrono</span><span style="color: #12B868; font-size: 24px; font-family: Lato; font-weight: 700; text-transform: uppercase; word-wrap: break-word">Mate</span></div></a>
        </div>
    </nav>
</header>

<!-- Overlay and modal for displaying error messages -->
<div id="overlay" class="overlay"></div>
<div id="modal" class="myAlert">
    <div id="modalContent" class="myAlertBody">
        <h4>Error</h4>
        <p class="pt-2" id="modalText"></p>
        <button class="btn btn-danger float-end" onclick="closeModal()">Close</button>
    </div>
</div>

<!-- Container for the login form -->
<div class="container py-5" style="margin-top: 100px">
    <div class="row py-5 justify-content-center">
        <div class="col-md-8 py-2">
            <div class="card">
                <div class="card-body">
                    <h2 class="card-title text-center py-3">Login</h2>
                    <!-- Form for user login -->
                    <form id="LoginForm" action="/login" method="post">
                        <div class="input-group mb-3">
                            <input id="email_or_username" name="username" type="text" class="form-control" placeholder="Email or Username" aria-label="Email or Username">
                            <input type="radio" class="btn-check" name="options-base" id="option1" autocomplete="off" checked onclick="selectUsername()">
                            <label class="btn btn-outline-success" for="option1">Username</label>
                            <input type="radio" class="btn-check" name="options-base" id="option2" autocomplete="off"  onclick="selectEmail()">
                            <label class="btn btn-outline-success" for="option2">Email</label>
                        </div>
                        <div class="mb-3">
                            <label for="password" class="form-label">Password</label>
                            <input type="password" class="form-control" name="password" id="password" required title="Required length: 8 characters and at least one number, one uppercase letter, and one special character"/>
                            <a href="/signup" class="mt-3">Not registered? Sign Up</a>
                        </div>
                        <div class="mt-5 text-end">
                            <!-- Button to submit the form with a js function witch check all the fields-->
                            <a class="btn custom-btn-color" onclick="submitLoginForm()" >Login</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>




<!-- Included footer component -->
<%@ include file="components/footer.txt"%>

<!-- JavaScript code for handling error messages and form submission -->
<script>

    window.addEventListener('load', () => {
        // Display modal with error message if present
        let message = "${errorMessage}";
        if(message !== "" && message !== null && message !== undefined) {
            displayModal(message);
        }
    });

    function submitLoginForm() {
        const usernamePattern = /^[A-Za-z0-9\s._-]+$/;
        const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
        const email_or_username = document.getElementById('email_or_username');
        if (email_or_username.getAttribute('name') === 'username') {
            if (email_or_username.value.trim() === '' || !usernamePattern.test(email_or_username.value)) {
                displayModal("Username is a required field. Only letters, numbers, spaces and '.' '_' '-' are allowed");
                return;
            }
        } else if (email_or_username.getAttribute('name') === 'email') {
            if (email_or_username.value.trim() === '' || !emailPattern.test(email_or_username.value)) {
                displayModal("Email is a required field. Use a valid email address");
                return;
            }
        }
        const password = document.getElementById('password');
        if (password.value.trim() === '') {
            displayModal("Password is a required field");
            return;
        }

        //if the url is /admin/login, submit the form to /admin/login
        if(window.location.pathname === "/admin/login") {
            document.getElementById("LoginForm").action = "/admin/login";
        }

        document.getElementById("LoginForm").submit();
    }

    // Set the attribute to 'username' when selecting username
    function selectUsername() {
        document.getElementById("email_or_username").setAttribute('name', 'username');
    }

    // Set the attribute to 'email' when selecting email
    function selectEmail() {
        document.getElementById("email_or_username").setAttribute('name', 'email');
    }
</script>
<!-- External scripts for jQuery, Cropper.js, Bootstrap, and custom JavaScript files -->
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script src="https://unpkg.com/cropperjs/dist/cropper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
<script src="/js/Main.js"></script>
</body>
</html>
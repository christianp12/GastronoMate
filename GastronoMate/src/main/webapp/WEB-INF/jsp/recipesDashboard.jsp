<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Recipe dashboard</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css" />

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400&family=Lato:wght@300;400&family=Playfair+Display+SC&display=swap" rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous" />
    <link rel="stylesheet" href="/css/main.css"/>

</head>
<body>
<header>



    <nav class="navbar custom-navbar-bg fixed-top">
        <div class="container-fluid px-4">
            <a class="navbar-brand" href="/"><div style="width: 100%"><span style="color: #0B5A18; font-size: 24px; font-family: Lato; font-weight: 700; text-transform: uppercase; word-wrap: break-word">Gastrono</span><span style="color: #12B868; font-size: 24px; font-family: Lato; font-weight: 700; text-transform: uppercase; word-wrap: break-word">Mate</span></div></a>
            <button class="navbar-toggler" type="button" data-bs-toggle="offcanvas" data-bs-target="#offcanvasDarkNavbar" aria-controls="offcanvasDarkNavbar" aria-label="Toggle navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
            <div class="offcanvas offcanvas-end" tabindex="-1" id="offcanvasDarkNavbar" aria-labelledby="offcanvasDarkNavbarLabel">
                <div class="offcanvas-header d-flex justify-content-center">
                    <h5 class="offcanvas-title" id="offcanvasDarkNavbarLabel">
                        <a class="navbar-brand" href="#">
                            <img
                                    src="/images\GastronomateMateLogo.svg"
                                    height="130rem"
                                    alt="GASTRONOMATE"
                            />
                        </a>
                    </h5>
                    <button type="button" class="btn-close ps-3" data-bs-dismiss="offcanvas" aria-label="Close"></button>
                </div>

                <div class="offcanvas-body">
                    <ul class="navbar-nav justify-content-end flex-grow-1 pe-3">
                        <li class="nav-item">
                            <a class="nav-link active" aria-current="page" href="/admin/">Home</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/admin/users/dashboard">Users Dashboard</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/admin/recipes/dashboard">Recipes Dashboard</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/admin/reviews/dashboard">Reviews Dashboard</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/logout">Logout</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    </nav>

    <main class="container mt-4" style="margin-bottom: 180px">


    </main>


<!-- Included footer component -->
<%@ include file="components/footer.txt"%>
<!-- External scripts for jQuery, Bootstrap, and custom JavaScript files -->
<script src="https://code.jquery.com/jquery-3.6.4.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
</body>
</html>

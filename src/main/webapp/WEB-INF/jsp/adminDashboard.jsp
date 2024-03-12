<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Dashboard</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css" />
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400&family=Lato:wght@300;400&family=Playfair+Display+SC&display=swap" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN" crossorigin="anonymous" />
    <link rel="stylesheet" href="/css/main.css"/>

    <style>
        .loader {
            border: 10px solid #f3f3f3;
            border-radius: 50%;
            border-top: 10px solid var(--green);
            width: 80px;
            height: 80px;
            animation: spin 2s linear infinite;
        }
        @keyframes spin {
            0% { transform: rotate(0deg); }
            100% { transform: rotate(360deg); }
        }
    </style>
</head>
<body>

<header>
    <%@ include file="components/adminNavbar.txt" %>
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

<main class="container" style="margin-top: 200px; margin-bottom: 180px">

    <h1 class="text-center" style="margin-bottom: 50px">Dashboard</h1>


    <div class="row my-2">
        <div class="col-8">
            <canvas id="monthlySubscriptions" width="400" height="200"></canvas>
        </div>
        <div class="col-4" style="margin-top: 60px">
            <label for="monthlySubscriptionsDate">Select data:</label>
            <input class="form-control" type="date" id="monthlySubscriptionsDate" style="max-width: 200px" >
            <button class="btn custom-btn-color mt-2" id="monthlySubscriptionsButton">Get Monthly Subscriptions Data</button>
        </div>
    </div>


    <div class="row mt-5">
        <div class="col-8">
            <canvas id="yearSubscriptions" width="400" height="200"></canvas>
        </div>
        <div class="col-4" style="margin-top: 60px">
            <label for="yearSubscriptionsDateStart">Start year:</label>
            <input class="form-control" type="date" id="yearSubscriptionsDateStart" style="max-width: 200px" min="1950-01-01" max="2023-01-01">
            <label for="yearSubscriptionsDateEnd">End year:</label>
            <input class="form-control" type="date" id="yearSubscriptionsDateEnd" style="max-width: 200px" min="1951-01-01" max="2024-02-20">
            <button class="btn custom-btn-color mt-2" id="yearSubscriptionsButton" onclick="YearSubscriptionsButton()">Get Year Subscriptions data</button>
        </div>
    </div>

    <div class="row mt-5">
        <div class="col-8">
            <canvas id="usersPerState" width="400" height="200"></canvas>
        </div>
        <div class="col-4" style="margin-top: 60px">
            <label for="usersPerStateDateStart">Start date:</label>
            <input class="form-control" type="date" id="usersPerStateDateStart" style="max-width: 200px" min="1950-01-01" max="2023-01-01">
            <label for="usersPerStateDateEnd">End date:</label>
            <input class="form-control" type="date" id="usersPerStateDateEnd" style="max-width: 200px" min="1951-01-01" max="2024-02-20">
            <button class="btn custom-btn-color mt-2" id="usersPerStateButton" onclick="UserPerStateButton()">Get User per state data</button>
        </div>
    </div>

    <div class="d-flex gap-3 align-items-center mt-5">
        <button class="btn custom-btn-color " id="influencersButton" onclick="showInfluencers()">Show influencers' table</button>
        <button class="btn custom-btn-color" id="popularKeywordsButton" onclick="showPopularKeywords(0)">Show popular keywords</button>
        <button class="btn custom-btn-color" id="bestScoredRecipesButton" onclick="showBestScoredRecipes()">Show best scored recipes</button>
        <button class="btn custom-btn-color" id="mostLikedRecipes" onclick="showMostLikedRecipes()">Show most liked recipes</button>
    </div>

    <div class="mt-5 container" id="influencersTable" style="display: none">
        <h1 class="text-center">Influencers</h1>
        <div class="row">
            <div class="col-12">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">Username</th>
                        <th scope="col">Picture</th>
                    </tr>
                    </thead>

                    <tbody id="influencersTableBody">
                    </tbody>
                </table>
            </div>
        </div>
        <button class="btn custom-btn-color mt-3" onclick="hideInfluencers()">Hide</button>
    </div>

    </div>
    <div class="mt-5 container" id="keywordsTable" style="display: none">
        <h1 class="text-center">Popular Keywords</h1>
        <span id="load" style="display: none">Loading</span>
        <div class="row" id="keywordsArea">
            <div class="col-12">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">Keyword</th>
                        <th scope="col">Count</th>
                    </tr>
                    </thead>
                    <tbody id="keywordsTableBody">
                    </tbody>
                </table>
            </div>
        </div>
        <div class="d-flex gap-3 align-items-center mt-3">
            <button class="btn custom-btn-color" onclick="hideKeywords()">Hide</button>
            <div class="d-flex gap-3 align-items-center w-100">
                <label for="popularKeywordsDateStart">Start date:</label>
                <input class="form-control" type="date" id="popularKeywordsDateStart" style="max-width: 200px" min="1950-01-01" max="2023-01-01">
                <label for="popularKeywordsDateEnd">End date:</label>
                <input class="form-control" type="date" id="popularKeywordsDateEnd" style="max-width: 200px" min="1951-01-01" max="2024-02-20">
                <button class="btn custom-btn-color "  onclick="showPopularKeywords(1)">Change period</button>
            </div>
        </div>
    </div>

    <div class="mt-5 container" id="bestScoredRecipesTable" style="display: none">
        <h1 class="text-center">Best scored recipes</h1>
        <div class="row">
            <div class="col-12">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">Recipe ID</th>
                        <th scope="col">Title</th>
                        <th scope="col">Author</th>
                        <th scope="col">Author Picture</th>
                        <th scope="col">Average Rating</th>
                    </tr>
                    </thead>
                    <tbody id="scoreTableBody">
                    </tbody>
                </table>
            </div>
        </div>
        <button class="btn custom-btn-color mt-3" onclick="hideScoreTable()">Hide</button>
    </div>

    <div class="mt-5 container" id="mostLikedRecipesTable" style="display: none">
        <h1 class="text-center">Most liked recipes</h1>
        <div class="row">
            <div class="col-12">
                <table class="table table-striped">
                    <thead>
                    <tr>
                        <th scope="col">Recipe ID</th>
                        <th scope="col">Title</th>
                        <th scope="col">Picture</th>
                        <th scope="col">Author</th>
                        <th scope="col">Author Picture</th>
                        <th scope="col">Likes</th>
                    </tr>
                    </thead>
                    <tbody id="likesTableBody">
                    </tbody>
                </table>
            </div>
        </div>
        <button class="btn custom-btn-color mt-3" onclick="hideLikesTable()">Hide</button>
    </div>

</main>

<%@ include file="components/footer.txt" %>

<script src="https://code.jquery.com/jquery-3.6.4.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

<script>


    let yearlySubscriptions = '${yearSubscriptions}';
    yearlySubscriptions = JSON.parse(yearlySubscriptions);

    let usersPerState = '${usersPerState}';
    usersPerState = JSON.parse(usersPerState);

    const monthlySubscriptions = ${monthlySubscriptions};
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];

    let monthlySubscriptionsCanvas;
    let yearSubscriptionsCanvas;
    let usersPerStateCanvas;

    document.getElementById('monthlySubscriptionsDate').addEventListener('change', function() {
        let selectedDate = this.value;
        document.getElementById('monthlySubscriptionsButton').setAttribute('onclick', 'getMonthlySubscriptionsData(\'' + selectedDate + '\')');
    });




    window.onload =() => {
        plotMonthlySubscriptionsData(monthlySubscriptions);
        plotYearSubscriptionsData(yearlySubscriptions);
        plotUserPerState(usersPerState);

        getInfluencers();
    };


    function plotMonthlySubscriptionsData(data) {
        // Check if the chart already exists and destroy it
        if (monthlySubscriptionsCanvas) {
            monthlySubscriptionsCanvas.destroy();
        }

        const ctx = document.getElementById('monthlySubscriptions').getContext('2d');

        // Create a new bar chart using Chart.js
        monthlySubscriptionsCanvas = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: monthNames,
                datasets: [{
                    label: 'Monthly Subscriptions Percentage',
                    data: Object.values(data),
                    backgroundColor: '#C8ECC2',
                    borderColor: '#105241',
                    borderWidth: 1
                }]
            },
            options: {
                plugins: {
                    title: {
                        display: true,
                        text: 'Monthly Subscriptions Percentage',
                        font: {
                            size: 16
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100
                    }
                }
            }
        });
    }

    function getMonthlySubscriptionsData(start) {
        // Perform asynchronous request using fetch
        fetch('/admin/monthlySubscriptions?start=' + start)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error during the request: ' + response.status + ' ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                plotMonthlySubscriptionsData(data);
            })
            .catch(error => {
                displayModal("An error occurred");
            });
        document.getElementById('monthlySubscriptions').scrollIntoView({ behavior: 'smooth' });
    }

    function getYearSubscriptionsData(yearStart, yearEnd) {
        // Perform asynchronous request using fetch
        fetch('/admin/yearSubscriptions?yearStart=' + yearStart + '&yearEnd=' + yearEnd)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error during the request: ' + response.status + ' ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                plotYearSubscriptionsData(data);
            })
            .catch(error => {
                displayModal("An error occurred");
            });
    }

    function YearSubscriptionsButton() {
        // Get the values of the start and end dates
        const startDateInput = document.getElementById('yearSubscriptionsDateStart');
        const endDateInput = document.getElementById('yearSubscriptionsDateEnd');
        let startYear = startDateInput.value;
        let endYear = endDateInput.value;

        // Check that the dates are not null or empty
        if (startYear !== null && endYear !== null && startYear !== '' && endYear !== '') {
            // Extract the year from the dates
            startYear = startYear.split('-')[0];
            endYear = endYear.split('-')[0];

            // Check that the start date is before or equal to the end date
            if (parseInt(startYear) <= parseInt(endYear)) {

                // Check that the dates are within the limits set by the min and max attributes
                if (startDateInput.checkValidity() && endDateInput.checkValidity()) {
                    // If the dates are valid and within the limits, proceed with the function call
                    getYearSubscriptionsData(startYear, endYear);
                } else {
                    displayModal("Please enter valid dates within the specified range.");
                }
            } else {
                // Show an error message if the start year is after the end year
                displayModal("Start year must be before or equal to end year.");
            }
        } else {
            // Show an error message if either start or end year is not provided
            displayModal("Please fill in both start and end years.");
        }
    }




    function plotYearSubscriptionsData(data) {
        // Check if the chart already exists and destroy it
        if (yearSubscriptionsCanvas) {
            yearSubscriptionsCanvas.destroy();
        }

        let ctx = document.getElementById('yearSubscriptions').getContext('2d');
        yearSubscriptionsCanvas = new Chart(ctx, {
            type: 'line',
            data: {
                labels: Object.keys(data),
                datasets: [{
                    label: 'Subscriptions per year',
                    data: Object.values(data),
                    backgroundColor: 'rgba(82, 183, 136, 0.2)', // green
                    borderColor: 'rgba(16, 82, 65, 1)', // dark-green
                    borderWidth: 1
                }]
            },
            options: {
                plugins: {
                    title: {
                        display: true,
                        text: 'Subscription Trend',
                        font: {
                            size: 16
                        }
                    }
                }
            }
        });
    }

    function plotUserPerState(data) {
        // Check if the chart already exists and destroy it
        if (usersPerStateCanvas) {
            usersPerStateCanvas.destroy();
        }

        let ctx = document.getElementById('usersPerState').getContext('2d');
        usersPerStateCanvas = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: Object.keys(data),
                datasets: [{
                    label: 'Users per state',
                    data: Object.values(data),
                    backgroundColor: '#C8ECC2',
                    borderColor: '#105241',
                    borderWidth: 1
                }]
            },
            options: {
                plugins: {
                    title: {
                        display: true,
                        text: 'User Distribution by State',
                        font: {
                            size: 16
                        }
                    }
                }
            }
        });
    }

    function getUserPerStateData(dateStart, dateEnd) {
        // Perform asynchronous request using fetch
        fetch('/admin/usersPerState?start=' + dateStart + '&end=' + dateEnd)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Error during the request: ' + response.status + ' ' + response.statusText);
                }
                console.log(response);
                return response.json();
            })
            .then(data => {
                plotUserPerState(data);
            })
            .catch(error => {
                displayModal("An error occurred"+ error);
            });
    }

    function UserPerStateButton() {
        // Get the values of the start and end dates
        const startDateInput = document.getElementById('usersPerStateDateStart');
        const endDateInput = document.getElementById('usersPerStateDateEnd');
        let startDate = startDateInput.value;
        let endDate = endDateInput.value;

        // Check that the dates are not null or empty
        if (startDate !== null && endDate !== null && startDate !== '' && endDate !== '') {

            // Check that the start date is before or equal to the end date
            if (parseInt(startDate) <= parseInt(endDate)) {

                // Check that the dates are within the limits set by the min and max attributes
                if (startDateInput.checkValidity() && endDateInput.checkValidity()) {
                    // If the dates are valid and within the limits, proceed with the function call
                    getUserPerStateData(startDate, endDate);
                } else {
                    displayModal("Please enter valid dates within the specified range.");
                }
            } else {
                // Show an error message if the start year is after the end year
                displayModal("Start year must be before or equal to end year.");
            }
        } else {
            // Show an error message if either start or end year is not provided
            displayModal("Please fill in both start and end years.");
        }
    }

    function getInfluencers() {

        //fetch influencers from the server
        fetch('/admin/influencers')
            .then(response => {

                if (!response.ok) {
                    throw new Error('Error during the request: ' + response.status + ' ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                let influencersTableBody = document.getElementById('influencersTableBody');

                influencersTableBody.innerHTML = '';

                data.forEach(influencer => {
                    let row = document.createElement('tr');
                    let username = document.createElement('td');
                    username.textContent = influencer.username;
                    let picture = document.createElement('td');
                    let img = document.createElement('img');

                    if (influencer.profilePictureUrl !== undefined)
                        img.src = '/uploads/' + influencer.profilePictureUrl;
                    else
                        img.src = '/images/userLogo.svg';

                    img.style.width = '50px';
                    picture.appendChild(img);
                    row.appendChild(username);
                    row.appendChild(picture);
                    influencersTableBody.appendChild(row);
                });

            })
            .catch(error => {
                displayModal("An error occurred");
            });
    }

    function showInfluencers() {
        document.getElementById('influencersTable').style.display = 'block';
    }

    function hideInfluencers() {

        document.getElementById('influencersTable').style.display = 'none';
    }

    function showPopularKeywords(code) {

      if(code === 1){
          let startDate = document.getElementById('popularKeywordsDateStart').value;
          let endDate = document.getElementById('popularKeywordsDateEnd').value;

          if(!checkDate(startDate, endDate)){
              return;
          }

          //hide keywords Area
          document.getElementById('keywordsArea').style.display = 'none';

          //show loading text
          document.getElementById('load').style.display = 'block';

          //create an animation of dots
          let dots = window.setInterval( function() {
              let wait = document.getElementById('load');
              if ( wait.innerHTML.length > 3 )
                  wait.innerHTML = "Loading";
              else
                  wait.innerHTML += ".";
          }, 300);

          fetch('/admin/popularKeywords?start=' + startDate + '&end=' + endDate)
              .then(response => {
                  if (!response.ok) {
                      throw new Error('Error during the request: ' + response.status + ' ' + response.statusText);
                  }
                  return response.json();
              })
              .then(data => {

                  let keywordsTableBody = document.getElementById('keywordsTableBody');
                  keywordsTableBody.innerHTML = '';

                  for (var [key, value] of Object.entries(data)) {
                      let row = document.createElement('tr');
                      let keywordCell = document.createElement('td');
                      keywordCell.textContent = key;
                      let countCell = document.createElement('td');
                      countCell.textContent = value;
                      row.appendChild(keywordCell);
                      row.appendChild(countCell);
                      keywordsTableBody.appendChild(row);

                  }
                  //show the keyword Area
                  document.getElementById('keywordsArea').style.display = 'block';

                  //hide loading text
                  document.getElementById('load').style.display = 'none';

                  //stop the animation of dots
                  clearInterval(dots);
              })
              .catch(error => {
                  displayModal("An error occurred");
              });
      }
      else if(code === 0){
          //if the tableBody has children, do not fetch the keywords again
          if (document.getElementById('keywordsTableBody').children.length > 0) {
              document.getElementById('keywordsTable').style.display = 'block';
              return;
          }

          //fetch popular keywords from the server
          fetch('/admin/popularKeywords')
              .then(response => {

                  if (!response.ok) {
                      throw new Error('Error during the request: ' + response.status + ' ' + response.statusText);
                  }
                  return response.json();
              })
              .then(data => {
                  let keywordsTableBody = document.getElementById('keywordsTableBody');

                  for (var [key, value] of Object.entries(data)) {

                      let row = document.createElement('tr');
                      let keywordCell = document.createElement('td');
                      keywordCell.textContent = key;
                      let countCell = document.createElement('td');
                      countCell.textContent = value;
                      row.appendChild(keywordCell);
                      row.appendChild(countCell);
                      keywordsTableBody.appendChild(row);

                  }
                  document.getElementById('keywordsTable').style.display = 'block';
              })
              .catch(error => {
                  displayModal("An error occurred");
              });
      }
    }
    function checkDate(startDate, endDate){
        if (startDate !== null && endDate !== null && startDate !== '' && endDate !== '') {

            // Check that the start date is before or equal to the end date
            if (startDate <= endDate) {
                return true;
            } else {
                // Show an error message if the start year is after the end year
                displayModal("Start date must be before or equal to end date.");
                return false;
            }
        } else {
            // Show an error message if either start or end year is not provided
            displayModal("Please fill in both start and end dates.");
            return false;
        }
    }

    function hideKeywords() {
        document.getElementById('keywordsTable').style.display = 'none';
    }

    function showBestScoredRecipes() {

        //if the tableBody has children, do not fetch the keywords again
        if (document.getElementById('scoreTableBody').children.length > 0) {
            document.getElementById('bestScoredRecipesTable').style.display = 'block';
            return;
        }

        //fetch popular keywords from the server
        fetch('/admin/bestScoredRecipes')
            .then(response => {

                if (!response.ok) {
                    throw new Error('Error during the request: ' + response.status + ' ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                let scoreTableBody = document.getElementById('scoreTableBody');

                data.forEach(recipe => {
                    let row = document.createElement('tr');
                    let recipeIdCell = document.createElement('td');
                    recipeIdCell.textContent = recipe.recipeId;
                    let titeCell = document.createElement('td');
                    titeCell.textContent = recipe.title;
                    let authorCell = document.createElement('td');
                    authorCell.textContent = recipe.authorUsername;
                    let authorPictureCell = document.createElement('td');
                    let img = document.createElement('img');
                    if(recipe.authorProfilePictureUrl !== undefined)
                        img.src = '/uploads/' + recipe.authorProfilePictureUrl;
                    else
                        img.src = '/images/userLogo.svg';
                    img.style.width = '50px';
                    authorPictureCell.appendChild(img);

                    let scoreCell = document.createElement('td');
                    scoreCell.textContent = recipe.averageRating;

                    row.appendChild(recipeIdCell);
                    row.appendChild(titeCell);
                    row.appendChild(authorCell);
                    row.appendChild(authorPictureCell);
                    row.appendChild(scoreCell);
                    scoreTableBody.appendChild(row);

                });

                document.getElementById('bestScoredRecipesTable').style.display = 'block';
            })
            .catch(error => {
                displayModal("An error occurred");
            });
    }

    function hideScoreTable() {
        document.getElementById('bestScoredRecipesTable').style.display = 'none';
    }

    function showMostLikedRecipes() {

        //if the tableBody has children, do not fetch the keywords again
        if (document.getElementById('likesTableBody').children.length > 0) {
            document.getElementById('mostLikedRecipesTable').style.display = 'block';
            return;
        }

        //fetch popular keywords from the server
        fetch('/admin/mostLikedRecipes')
            .then(response => {

                if (!response.ok) {
                    throw new Error('Error during the request: ' + response.status + ' ' + response.statusText);
                }
                return response.json();
            })
            .then(data => {
                let likesTableBody = document.getElementById('likesTableBody');

                data.forEach(recipe => {
                    let row = document.createElement('tr');
                    let recipeIdCell = document.createElement('td');
                    recipeIdCell.textContent = recipe.recipeId;
                    let titeCell = document.createElement('td');
                    titeCell.textContent = recipe.title;
                    let pictureCell = document.createElement('td');
                    let img = document.createElement('img');
                    if(recipe.pictureUrl !== undefined){
                        if(recipe.pictureUrl.startsWith('http'))
                            img.src = recipe.pictureUrl;
                        else
                            img.src = '/uploads/' + recipe.pictureUrl;
                    }
                    else
                        img.src = '/images/GastronomateMateLogo.svg';
                    img.style.width = '50px';
                    pictureCell.appendChild(img);
                    let authorCell = document.createElement('td');
                    authorCell.textContent = recipe.authorUsername;
                    let authorPictureCell = document.createElement('td');
                    let img2 = document.createElement('img');
                    if(recipe.authorProfilePictureUrl !== undefined)
                        img2.src = '/uploads/' + recipe.authorProfilePictureUrl;
                    else
                        img2.src = '/images/userLogo.svg';
                    img2.style.width = '50px';
                    authorPictureCell.appendChild(img2);
                    let likesCell = document.createElement('td');
                    likesCell.textContent = recipe.likes;

                    row.appendChild(recipeIdCell);
                    row.appendChild(titeCell);
                    row.appendChild(pictureCell);
                    row.appendChild(authorCell);
                    row.appendChild(authorPictureCell);
                    row.appendChild(likesCell);
                    likesTableBody.appendChild(row);

                });

                document.getElementById('mostLikedRecipesTable').style.display = 'block';
            })
            .catch(error => {
                displayModal("An error occurred");
            });
    }

    function hideLikesTable() {
        document.getElementById('mostLikedRecipesTable').style.display = 'none';
    }


</script>
<script src="/js/Main.js"></script>
</body>
</html>
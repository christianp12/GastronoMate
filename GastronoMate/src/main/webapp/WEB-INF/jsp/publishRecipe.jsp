<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Publish a recipe</title>
    <!-- External stylesheets for icons and fonts -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css"/>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400&family=Lato:wght@300;400&family=Playfair+Display+SC&display=swap" rel="stylesheet">
    <!-- Bootstrap stylesheet -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"  crossorigin="anonymous"/>
    <!-- Cropper stylesheet -->
    <link rel="stylesheet" href="https://unpkg.com/cropperjs/dist/cropper.min.css" />
    <!-- Custom stylesheet -->
    <link rel="stylesheet" href="/css/main.css"/>

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
            border-radius: 20px;
        }

        body {
            overflow-y: scroll;
        }

    </style>
</head>
<body>

<%--Navbar component--%>
<%@ include file="components/normalNavbar.txt" %>

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

<div class="container mt-5 mb-5">
    <div class="row justify-content-center py-5">
        <div class="col-md-8">
            <div class="card">
                <div class="card-body">
                    <h2 class="card-title text-center">Publish a recipe</h2>
                    <!-- Sign Up Form -->
                    <form action="/recipe/publish" method="post" id="PublishForm">
                        <div class="mb-3">
                            <label for="Title" class="form-label my-1">Title</label>
                            <input type="text" class="form-control" name="title" id="Title" required pattern="[A-Za-z0-9\s]+" title="Only letters, numbers, and spaces are allowed"/>

                            <label for="Description" class="form-label my-1">Description</label>
                            <textarea class="form-control" name="description" id="Description" required title="Only letters, numbers, and spaces are allowed"></textarea>

                            <label for="RecipeServings" class="form-label my-1">Recipe Servings</label>
                            <input type="number" class="form-control" step="1" min="0" name="recipeServings" id="RecipeServings"/>

                            <label for="CookTime" class="form-label my-1">Cooking Time in minutes</label>
                            <input type="number" class="form-control" step="1" min="0" name="cookTime" id="CookTime"/>

                            <label for="PrepTime" class="form-label my-1">Preparation Time in minutes</label>
                            <input type="number" class="form-control" step="1" min="0" name="prepTime" id="PrepTime"/>

                            <label for="uploadRowImage" class="form-label my-1">Recipe picture</label>
                            <img class="py-3" id="CroppedImage" style="display: none; max-height: 100%; width: 300px; border-radius: 15px" alt=""  src=""/>
                            <input type="hidden" name="croppedImageB64" id="croppedImageB64" value=""/>

                            <div class="input-group">
                                <input id="uploadRowImage" type="file" class="form-control" placeholder="Choose a recipe picture" aria-label="Choose a recipe picture" aria-describedby="button-addon2" accept="image/*">
                                <a class="btn btn-outline-danger" type="button" onclick="removeImage()">Remove image</a>
                            </div>

                            <div id="keywordsContainer">
                                <button type="button" class="btn custom-btn-color btn-sm my-3" onclick="addGroup('keywordsContainer', 'Keywords')">
                                    Add Keywords
                                </button>
                            </div>
                            <input type="hidden" name="listOfKeywords" id="listOfKeywords" style="display: none" value=""/>

                            <div id="ingredientsContainer">
                                <button type="button" class="btn custom-btn-color btn-sm my-3" onclick="addGroup('ingredientsContainer', 'Ingredients')">
                                    Add ingredients
                                </button>
                            </div>
                            <input type="hidden" name="listOfIngredients" id="listOfIngredients" style="display: none" value=""/>


                            <label class="form-label my-1" for="calories">Calories:</label>
                            <input class="form-control" type="number" step="0.1" min="0" name="calories" id="calories"/>

                            <label class="form-label my-1" for="fatContent">Fat Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" name="fatContent" id="fatContent"/>

                            <label class="form-label my-1" for="saturatedFatContent">Saturated Fat Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" name="saturatedFatContent" id="saturatedFatContent"/>

                            <label class="form-label my-1" for="sodiumContent">Sodium Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" name="sodiumContent" id="sodiumContent"/>

                            <label class="form-label my-1" for="carbohydrateContent">Carbohydrate Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" name="carbohydrateContent" id="carbohydrateContent"/>

                            <label class="form-label my-1" for="fiberContent">Fiber Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" name="fiberContent" id="fiberContent"/>

                            <label class="form-label my-1" for="sugarContent">Sugar Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" name="sugarContent" id="sugarContent"/>

                            <label class="form-label my-1" for="proteinContent">Protein Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" name="proteinContent" id="proteinContent"/>

                        </div>


                        <div class="mt-5 text-end">
                            <a class="btn custom-btn-color" onclick="submitForm()">Publish</a>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </div>
</div>

<script>
    function submitForm() {
        const pattern = /^[A-Za-z0-9\s]+$/;
        const descriptionPattern = /^[A-Za-z0-9\s.,;!?]+$/;

        // Check if the fields are valid
        const titleValue = document.getElementById("Title");
        const descriptionValue = document.getElementById("Description").value.trim();

        if (!titleValue.checkValidity()){
            displayModal("Title is a required field. Only letters, numbers, and spaces are allowed");
            return;
        }

        if (!descriptionPattern.test(descriptionValue)) {
            displayModal("Description is a required field. Special characters are not allowed.");
            return;
        }

        try {
            document.getElementById("listOfIngredients").value = collect("ingredientsContainer", "Ingredients");
        } catch (error) {
            displayModal(error.message);
            return;
        }

        try {
            document.getElementById("listOfKeywords").value = collect("keywordsContainer", "Keywords");

        } catch (error) {
            displayModal(error.message);
            return;
        }

        // if the pattern is valid, submit the form
        document.getElementById("PublishForm").submit();
    }



    function addGroup(groupId, groupName) {
        pattern = /^[A-Za-z0-9\s]*$/;
        const container = document.getElementById(groupId);

        const newGroup = document.createElement("div");
        newGroup.className = "input-group mb-2" + groupId + "Group";

        const newSpan = document.createElement("span");
        newSpan.className = "input-group-text";
        newSpan.textContent = groupName;
        newGroup.appendChild(newSpan);

        const group_id = groupName + container.childElementCount;
        newGroup.id = group_id;

        for (let i = 1; i <= 4; i++) {
            const newInput = document.createElement("input");
            newInput.type = "text";
            newInput.className = "form-control";
            newInput.setAttribute("aria-label", group_id + "_" + i);
            newInput.setAttribute("id", group_id + "_key_" + i);
            newGroup.appendChild(newInput);
        }
        container.appendChild(newGroup);
    }

    function collect(id, name) {
        const container = document.getElementById(id);

        if (!container) {
            console.error("Element with id " + id + " not found");
            throw new Error("Element with id " + id + " not found");
        }

        let list_of_input = "";


        const groupInputs = container.getElementsByTagName("input");

        if (!groupInputs) {
            console.error("No input found");
            throw new Error("No input found");
        }

        for (let i = 0; i < groupInputs.length; i++) {
            let groupInput = groupInputs[i];
            if (groupInput.value.trim() !== "") {
                if (!pattern.test(groupInput.value.trim())) {

                    throw new Error(
                        "Only letters, numbers, and spaces are allowed in " + name
                    );
                }
                list_of_input += groupInput.value + ",";
            }
        }

        if (list_of_input.endsWith(",")) {
            list_of_input = list_of_input.slice(0, -1);
        }

        console.log(list_of_input);

        return list_of_input;
    }

</script>

<!-- Included footer component -->
<%@ include file="components/footer.txt"%>

<!-- External scripts for jQuery, Bootstrap, and custom JavaScript files -->
<script src="https://code.jquery.com/jquery-3.6.4.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
<script src="https://unpkg.com/cropperjs/dist/cropper.min.js"></script>
<script src="/js/Main.js"></script>
<script src="/js/StarScript.js"></script>
</body>
</html>

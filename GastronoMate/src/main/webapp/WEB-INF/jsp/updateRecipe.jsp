<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page session="false" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Recipe update</title>
    <!-- External stylesheets for icons and fonts -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.1/css/all.min.css"/>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400&family=Lato:wght@300;400&family=Playfair+Display+SC&display=swap" rel="stylesheet">
    <!-- Bootstrap stylesheet -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-T3c6CoIi6uLrA9TneNEoa7RxnatzjcDSCmG1MXxSR1GAsXEV/Dwwykc2MPK8M2HN"  crossorigin="anonymous"/>
    <!-- Custom stylesheet -->
    <link rel="stylesheet" href="/css/main.css"/>
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

        <img id="RowImage" style="display: none; max-width: 100%; height: 500px; " alt="" />
        <button class="btn btn-primary mt-2" onclick="submitCrop()">Submit</button>
        <button class="btn btn-danger mt-2" onclick="cancelCrop()">Cancel</button>
    </div>
</div>

<div class="container mt-5" style="min-height: 80vh; margin-bottom: 180px">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-body">
                    <h2 class="card-title text-center">Update a recipe</h2>

                    <form action="/recipe/edit" method="post" id="EditForm">
                        <div class="mb-3">
                            <label for="Title" class="form-label my-1">Title</label>
                            <input type="text" class="form-control" name="title" id="Title"  placeholder="${recipe.title}"  title="Only letters, numbers, and spaces are allowed"/>

                            <label for="Description" class="form-label my-1">Description</label>
                            <textarea class="form-control" name="description" id="Description" placeholder="${recipe.description}" title="Only letters, numbers, and spaces are allowed"></textarea>

                            <label for="RecipeServings" class="form-label my-1">Recipe Servings</label>
                            <input type="number" class="form-control" step="1" min="0" placeholder="${recipe.recipeServings}" name="recipeServings" id="RecipeServings"/>

                            <label for="CookTime" class="form-label my-1">Cooking Time in minutes</label>
                            <input type="number" class="form-control" placeholder="${recipe.cookTime}" step="1" min="0" name="cookTime" id="CookTime"/>

                            <label for="PrepTime" class="form-label my-1">Preparation Time in minutes</label>
                            <input type="number" class="form-control" placeholder="${recipe.prepTime}" step="1" min="0" name="prepTime" id="PrepTime"/>

                            <label for="uploadRowImage" class="form-label my-1">Recipe picture</label>

                            <img class="py-3" id="CroppedImage" style="display: none; max-height: 100%; width: 300px; border-radius: 15px" alt=""  src="${recipe.pictureUrl}"/>

                            <input type="hidden" name="croppedImageB64" id="croppedImageB64" value=""/>

                            <div class="input-group">
                                <input id="uploadRowImage" type="file" class="form-control" placeholder="Choose a recipe picture" aria-label="Choose a recipe picture" aria-describedby="button-addon2" accept="image/*">
                                <a class="btn btn-outline-danger" type="button" onclick="removeImage()">Remove image</a>
                            </div>

                            <div id="keywordsContainer">
                                <button type="button" class="btn custom-btn-color btn-sm my-3" onclick="addGroup('keywordsContainer', 'Keywords', '')">
                                    Add Keywords
                                </button>
                            </div>
                            <input type="hidden" name="listOfKeywords" id="listOfKeywords" style="display: none" value=""/>

                            <div id="ingredientsContainer">
                                <button type="button" class="btn custom-btn-color btn-sm my-3" onclick="addGroup('ingredientsContainer', 'Ingredients', '')">
                                    Add ingredients
                                </button>
                            </div>
                            <input type="hidden" name="listOfIngredients" id="listOfIngredients" style="display: none" value=""/>


                            <label class="form-label my-1" for="calories">Calories:</label>
                            <input class="form-control" type="number" step="0.1" min="0" placeholder="${recipe.calories}" name="calories" id="calories"/>

                            <label class="form-label my-1" for="fatContent">Fat Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" placeholder="${recipe.fatContent}" name="fatContent" id="fatContent"/>

                            <label class="form-label my-1" for="saturatedFatContent">Saturated Fat Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" placeholder="${recipe.saturatedFatContent}" name="saturatedFatContent" id="saturatedFatContent"/>

                            <label class="form-label my-1" for="sodiumContent">Sodium Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" placeholder="${recipe.sodiumContent}" name="sodiumContent" id="sodiumContent"/>

                            <label class="form-label my-1" for="carbohydrateContent">Carbohydrate Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" placeholder="${recipe.carbohydrateContent}" name="carbohydrateContent" id="carbohydrateContent"/>

                            <label class="form-label my-1" for="fiberContent">Fiber Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" placeholder="${recipe.fiberContent}" name="fiberContent" id="fiberContent"/>

                            <label class="form-label my-1" for="sugarContent">Sugar Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" placeholder="${recipe.sugarContent}" name="sugarContent" id="sugarContent"/>

                            <label class="form-label my-1" for="proteinContent">Protein Content:</label>
                            <input class="form-control" type="number" step="0.1" min="0" placeholder="${recipe.proteinContent}" name="proteinContent" id="proteinContent"/>

                        </div>

                        <input type="hidden" name="recipeId" value="${recipe.recipeId}">
                        <input type="hidden" name="oldImage" value="${recipe.pictureUrl}">

                        <div class="mt-5 text-end">
                            <a class="btn custom-btn-color" onclick="submitForm()">Edit</a>
                        </div>
                    </form>

                </div>
            </div>
        </div>
    </div>
</div>
<%@ include file="components/footer.txt"%>

<script>

    let pattern = /^[A-Za-z0-9\s]+$/;
    let listOfKeywords = ${recipe.toJsArray(recipe.keywords)};
    let listOfIngredients = ${recipe.toJsArray(recipe.ingredients)};


    for (let i = 0; i < listOfKeywords.length/4; i++) {
        addGroup("keywordsContainer", "Keywords", listOfKeywords.slice(i*4, i*4+4));
    }

    for (let i = 0; i < listOfIngredients.length/3; i++) {
        addGroup("ingredientsContainer", "Ingredients", listOfIngredients.slice(i*4, i*4+4));
    }

    function submitForm() {

        // Check if the fields are valid
        const titleValue = document.getElementById("Title");
        const descriptionValue = document.getElementById("Description").value.trim();

        if ( titleValue.value.trim() !== "" && !pattern.test(titleValue.value)){
            displayModal("Only letters, numbers, and spaces are allowed");
            return;
        }

        if ( descriptionValue.trim() !== "" && !pattern.test(descriptionValue)) {
            displayModal("Only letters, numbers, and spaces are allowed");
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
        document.getElementById("EditForm").submit();
    }



    function addGroup(groupId, groupName, values) {
        var container = document.getElementById(groupId);


        var newGroup = document.createElement("div");
        newGroup.className = "input-group mb-2" + groupId + "Group";


        var newSpan = document.createElement("span");
        newSpan.className = "input-group-text";
        newSpan.textContent = groupName;
        newGroup.appendChild(newSpan);


        var group_id = groupName + container.childElementCount;
        newGroup.id = group_id;

        // Aggiungi i nuovi input al gruppo
        for (var i = 1; i <= 3; i++) {
            var newInput = document.createElement("input");
            newInput.type = "text";
            newInput.className = "form-control";
            newInput.setAttribute("aria-label", group_id + "_" + i);
            newInput.setAttribute("id", group_id + "_key_" + i);
            if (values[i-1] !== undefined) {
                newInput.setAttribute("value", values[i-1]);
            }
            newGroup.appendChild(newInput);
        }


        container.appendChild(newGroup);
    }

    function collect(id, name) {
        const container = document.getElementById(id);

        if (!container) {
            console.error("Element with id " + id + " not found");
            return;
        }

        var list_of_input = "";



        var groupInputs = container.getElementsByTagName("input");

        if (!groupInputs) {
            console.error("No input found");
            return;
        }

        for (let i = 0; i < groupInputs.length; i++) {
            let groupInput = groupInputs[i];
            if (groupInput.value.trim() !== "") {
                if (!pattern.test(groupInput.value)) {
                    displayModal(
                        "Only letters, numbers, and spaces are allowed in " + name
                    );
                    return;
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
<!-- External scripts for jQuery, Bootstrap, and custom JavaScript files -->
<script src="https://code.jquery.com/jquery-3.6.4.min.js"></script>
<script src="https://unpkg.com/cropperjs/dist/cropper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js" integrity="sha384-C6RzsynM9kWDrMNeT87bh95OGNyZPhcTNXj1NW7RuBCsyN/o0jlpcV8Qyq46cDfL" crossorigin="anonymous"></script>
<script src="/js/Main.js"></script>
<script src="/js/StarScript.js"></script>
</body>
</html>

function displayModal(message) {
    // Set the modal message content
    const overlayElement = document.getElementById("overlay");
    overlayElement.style.display = "block";
    const modalTextElement = document.getElementById("modalText");
    modalTextElement.innerText = message;

    //overflow-y: hidden;
    let body = document.getElementsByTagName("body")[0];
    body.style.overflowY = "hidden";

    // Show the modal
    const modalElement = document.getElementById("modal");
    modalElement.style.display = "block";
}

function displayModal_with_Title(title, message) {
    // Set the modal message content
    const overlayElement = document.getElementById("overlay");
    overlayElement.style.display = "block";
    const modalTitleElement = document.getElementById("modalTitle");
    const modalTextElement = document.getElementById("modalText");
    modalTitleElement.innerText = title;
    modalTextElement.innerText = message;


    //overflow-y: hidden;
    let body = document.getElementsByTagName("body")[0];
    body.style.overflowY = "hidden";

    // Show the modal
    const modalElement = document.getElementById("modal");
    modalElement.style.display = "block";
}

function closeModal() {
    // Close the modal
    //overflow-y: hidden;
    let body = document.getElementsByTagName("body")[0];
    body.style.overflowY = "scroll";
    const modalElement = document.getElementById("modal");
    modalElement.style.display = "none";
    const overlayElement = document.getElementById("overlay");
    overlayElement.style.display = "none";
}


var cropper;

// When an image is updated
document
    .getElementById("uploadRowImage")
    .addEventListener("change", function (e) {
        var input = e.target;

        if (input.files && input.files[0]) {
            // Assegna l'immagine letta direttamente all'elemento di output
            var immagineOutput = document.getElementById("RowImage");
            immagineOutput.src = URL.createObjectURL(input.files[0]);
            immagineOutput.style.display = "block";
        }

        openCropDialog();
    });

function openCropDialog() {
    // Ottieni riferimento alla finestra modale
    var cropDialog = document.getElementById("cropDialog");

    // Distruzione dell'istanza Cropper esistente se presente
    if (cropper) {
        cropper.destroy();
    }

    // Mostra la finestra modale
    cropDialog.style.display = "flex";

    // Assicurati che il Cropper sia inizializzato solo se l'immagine è stata caricata
    var rowImage = document.getElementById("RowImage");
    if (rowImage.src && rowImage.src !== "") {
        // Inizializza il Cropper sulla tua immagine
        cropper = new Cropper(rowImage, {
            aspectRatio: 1,
            crop: function (event) {
            },
        });
    }
}

function closeCropDialog() {
    // Nascondi la finestra modale
    var cropDialog = document.getElementById("cropDialog");
    cropDialog.style.display = "none";
}

function submitCrop(image) {
    // Logica per il pulsante Submit
    console.log("Submit clicked");

    // Ottieni l'immagine croppata dal Cropper.js
    var croppaData = cropper.getData();

    // Ottieni l'immagine originale
    var originalImage = document.getElementById("RowImage");

    // Crea un canvas temporaneo per il ritaglio
    var canvas = document.createElement("canvas");
    var ctx = canvas.getContext("2d");
    canvas.width = croppaData.width;
    canvas.height = croppaData.height;

    // Disegna l'immagine originale sul canvas con le coordinate di ritaglio
    ctx.drawImage(
        originalImage,
        croppaData.x,
        croppaData.y,
        croppaData.width,
        croppaData.height,
        0,
        0,
        croppaData.width,
        croppaData.height
    );

    // Ottieni l'URL dell'immagine croppata dal canvas
    var croppedImageURL = canvas.toDataURL();
    let b64Image = document.getElementById("croppedImageB64");
    b64Image.value = croppedImageURL;

    // Salva l'URL dell'immagine croppata nell'elemento con ID 'image'
    var imageElement = document.getElementById("CroppedImage");
    imageElement.src = croppedImageURL;
    imageElement.style.display = "block"; // Mostra l'elemento

    // Chiudi la finestra di ritaglio
    closeCropDialog();

    // Rimuovi il canvas per liberare le risorse
    document.body.removeChild(canvas);
}

function cancelCrop() {
    // Logica per il pulsante Cancel
    console.log("Cancel clicked");
    removeImage();
    closeCropDialog();
}

function removeImage() {
    // Nascondi l'immagine e reimposta il suo valore
    var croppedImage = document.getElementById("CroppedImage");
    croppedImage.style.display = "none";
    croppedImage.src = "";
}

function displayDeleteModal(message) {
    // Set the modal message content
    const overlayElement = document.getElementById("overlay");
    overlayElement.style.display = "block";
    const modalTextElement = document.getElementById("deleteModalText");
    modalTextElement.innerText = message;

    //overflow-y: hidden;
    let body = document.getElementsByTagName("body")[0];
    body.style.overflowY = "hidden";

    // Show the modal
    const modalElement = document.getElementById("deleteModal");
    modalElement.style.display = "block";
}

function closeDeleteModal() {
    // Close the modal
    //overflow-y: hidden;
    let body = document.getElementsByTagName("body")[0];
    body.style.overflowY = "scroll";
    const modalElement = document.getElementById("deleteModal");
    modalElement.style.display = "none";
    const overlayElement = document.getElementById("overlay");
    overlayElement.style.display = "none";
}

function nextPage(currentPage, numberOfPages) {
    const nextPage = parseInt(currentPage, 10) + 1;
    if (nextPage > numberOfPages) {
        return;
    }
    let old_url = window.location.href;
    window.location.href = old_url.replace(/\/\d+$/, '/' + nextPage);
}

function prevPage(currentPage) {
    const prevPage = parseInt(currentPage, 10) - 1;
    if (prevPage < 1) {
        return;
    }
    let old_url = window.location.href;
    window.location.href = old_url.replace(/\/\d+$/, '/' + prevPage);
}
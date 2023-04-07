// Get references to the form and its input fields
const form = document.querySelector('form');
const textInput = document.querySelector('#text-input');
const fileInput = document.querySelector('#file-input');

// Add a submit event listener to the form
form.addEventListener('submit', event => {
    // Prevent the form from submitting by default
    event.preventDefault();

    // Validate the user input
    const text = textInput.value.trim();
    const file = fileInput.files[0];

    if (!text && !file) {
        alert('Please enter some text or select a file');
        return;
    }

    if (text && file) {
        alert('Please enter text OR select a file, not both');
        return;
    }

    if (file && !file.type.match('text.*')) {
        alert('Please select a text file');
        return;
    }

    // If the input is valid, submit the form to the server
    const formData = new FormData();
    if (text) {
        formData.append('text', text);
    } else if (file) {
        formData.append('file', file);
    }

    const xhr = new XMLHttpRequest();
    xhr.open('POST', 'http://localhost:8080/upload');
    xhr.send(formData);
});

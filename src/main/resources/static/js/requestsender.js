// Get references to the form and its input fields
const form = document.querySelector('#plagiarism-form');
const textInput = document.querySelector('#text-input');
const fileInput = document.querySelector('#file-input');
const languageSelect = document.querySelector('#language-select');
const resultContainer = document.querySelector('#result-container');

// Add a submit event listener to the form
form.addEventListener('submit', event => {
    event.preventDefault();

    const text = textInput.value.trim();
    const file = fileInput.files[0];
    const language = languageSelect.value;

    // Validate the user input
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

    const formData = new FormData();
    if (text) {
        formData.append('text', text);
    } else if (file) {
        formData.append('file', file);
    }
    formData.append('lang', language);

    // const xhr = new XMLHttpRequest();
    // xhr.open('POST', '/upload');
    // xhr.onload = () => {
    //     if (xhr.status === 200) {
    //         const response = JSON.parse(xhr.responseText);
    //         resultContainer.innerHTML = `Plagiarism percentage: ${response.percentage}%<br>Matching URLs: ${response.urls.join(', ')}`;
    //     } else {
    //         resultContainer.innerHTML = 'An error occurred while checking for plagiarism';
    //     }
    // };
    // xhr.send(formData);

    const requestOptions = {
        method: 'POST',
        body: formData,
        headers: {
            'Accept': 'application/json'
        }
    };

    fetch('http://localhost:8080/upload', requestOptions)
        .then(response => response.json())
        .then(data => {
            console.log(data);

            // Create a new HTML page with the result
            const newPage = `
      <html>
        <head>
          <title>Plagiarism Checker Result</title>
          <style>
            .result-container {
              margin: 50px;
              padding: 20px;
              border: 2px solid #ccc;
              border-radius: 5px;
            }
            .result-container p {
              margin: 0;
            }
            .result-container h2 {
              margin-top: 0;
            }
            .urls-container {
              display: none;
              margin-top: 10px;
              border: 1px solid #ccc;
              padding: 5px;
            }
          </style>
        </head>
        <body>
          <div class="result-container">
            <h2>Plagiarism Checker Result</h2>
            <p>Plagiarism percentage: <strong>${data.percentage.toFixed(2)}%</strong></p>
            <p>Click below to view the URLs:</p>
            <button onclick="toggleUrls()">View URLs</button>
            <div class="urls-container" id="urls-container">
              <p>Similar URLs:</p>
              <ul>
                ${data.urls.map(url => `<li><a href="${url}" target="_blank">${url}</a></li>`).join('')}
              </ul>
            </div>
          </div>
          <script>
            function toggleUrls() {
              const urlsContainer = document.querySelector('#urls-container');
              urlsContainer.style.display = urlsContainer.style.display === 'none' ? 'block' : 'none';
            }
          </script>
        </body>
      </html>
    `;
    // Create a Blob object with the new HTML page
    const blob = new Blob([newPage], { type: 'text/html' });

    // Create a URL for the Blob object. Redirect to the new page
    window.location.href = URL.createObjectURL(blob);
        }).catch(error => console.log(error));
});

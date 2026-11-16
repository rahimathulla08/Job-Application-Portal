
const form = document.getElementById("applicationForm");

const API_URL = "http://localhost:8085/api/candidates";

form.addEventListener("submit", async function (event) {

    event.preventDefault();

    const message = document.getElementById("message");
    const submitBtn = document.getElementById("submitBtn");

    // Get selected job roles
    const roles = Array.from(
        document.querySelectorAll('input[name="roles"]:checked')
    ).map(role => role.value);

    // Get resume file
    const resume = document.getElementById("resume").files[0];

    // 1. Validate job roles
    if (roles.length === 0) {
        message.style.color = "red";
        message.textContent = "Please select at least one job role.";
        return;
    }

    // 2. Validate resume
    if (!resume) {
        message.style.color = "red";
        message.textContent = "Please upload your resume.";
        return;
    }

    // 3. Validate resume size (5 MB)
    if (resume.size > 5 * 1024 * 1024) {
        message.style.color = "red";
        message.textContent = "Resume must be less than 5 MB.";
        return;
    }

    // 4. Validate resume extension
    const allowedExtensions = ["pdf", "doc", "docx"];
    const extension = resume.name.split(".").pop().toLowerCase();

    if (!allowedExtensions.includes(extension)) {
        message.style.color = "red";
        message.textContent = "Please upload PDF, DOC or DOCX.";
        return;
    }

    // 5. Collect form data
    const formData = new FormData();

    formData.append("firstName",
        document.getElementById("firstName").value.trim());

    formData.append("lastName",
        document.getElementById("lastName").value.trim());

    formData.append("email",
        document.getElementById("email").value.trim());

    formData.append("phone",
        document.getElementById("phone").value.trim());

    formData.append("address",
        document.getElementById("address").value.trim());

    formData.append("skills",
        document.getElementById("skills").value.trim());

    formData.append("roles", roles.join(", "));

    formData.append("experienceLevel",
        document.getElementById("experienceLevel").value);

    formData.append("experience",
        document.getElementById("experience").value);

    formData.append("resume", resume);

    // 6. Send data to Spring Boot backend
    submitBtn.disabled = true;
    submitBtn.textContent = "Submitting...";

    message.style.color = "blue";
    message.textContent = "Submitting your application...";

    try {

        const response = await fetch(API_URL, {
            method: "POST",
            body: formData
        });

        const result = await response.text();

        if (!response.ok) {
            throw new Error(result || "Failed to submit application.");
        }

        // Success message
        message.style.color = "green";
        message.textContent =
            "Application submitted successfully!";

        console.log("Backend response:", result);

        // Clear form after successful submission
        form.reset();

    } catch (error) {

        console.error("Error:", error);

        message.style.color = "red";
        message.textContent =
            "Unable to submit application. Please check whether the backend is running.";

    } finally {

        submitBtn.disabled = false;
        submitBtn.textContent = "Submit Application";

    }

});

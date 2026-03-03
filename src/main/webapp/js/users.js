/* User Management - Client-side Logic */

function validatePasswordStrength(password) {
    var errors = [];
    if (password.length < 8) errors.push("At least 8 characters");
    if (!/[A-Z]/.test(password)) errors.push("At least one uppercase letter");
    if (!/[a-z]/.test(password)) errors.push("At least one lowercase letter");
    if (!/[0-9]/.test(password)) errors.push("At least one digit");
    if (!/[^a-zA-Z0-9]/.test(password)) errors.push("At least one special character");
    return { valid: errors.length === 0, errors: errors };
}

function showPasswordStrength(passwordInput, strengthBarId) {
    var bar = document.getElementById(strengthBarId);
    if (!bar) return;

    var password = passwordInput.value;
    var result = validatePasswordStrength(password);
    var score = 5 - result.errors.length;
    var percent = (score / 5) * 100;

    bar.style.width = percent + "%";

    if (percent <= 20) {
        bar.style.backgroundColor = "#dc3545";
    } else if (percent <= 40) {
        bar.style.backgroundColor = "#fd7e14";
    } else if (percent <= 60) {
        bar.style.backgroundColor = "#ffc107";
    } else if (percent <= 80) {
        bar.style.backgroundColor = "#20c997";
    } else {
        bar.style.backgroundColor = "#28a745";
    }
}

function checkPasswordMatch(passwordId, confirmId, messageId) {
    var pwd = document.getElementById(passwordId).value;
    var cfm = document.getElementById(confirmId).value;
    var msg = document.getElementById(messageId);

    if (cfm.length === 0) {
        msg.textContent = "";
        msg.style.display = "none";
        return true;
    }

    if (pwd !== cfm) {
        msg.textContent = "Passwords do not match";
        msg.style.color = "#dc3545";
        msg.style.display = "block";
        return false;
    } else {
        msg.textContent = "Passwords match";
        msg.style.color = "#28a745";
        msg.style.display = "block";
        return true;
    }
}

function validateCreateUserForm() {
    var pwd = document.getElementById("password").value;
    var cfm = document.getElementById("confirmPassword").value;

    if (pwd !== cfm) {
        alert("Passwords do not match!");
        document.getElementById("confirmPassword").focus();
        return false;
    }

    var result = validatePasswordStrength(pwd);
    if (!result.valid) {
        alert("Password does not meet requirements:\n- " + result.errors.join("\n- "));
        document.getElementById("password").focus();
        return false;
    }

    return true;
}

function validateResetPasswordForm() {
    var pwd = document.getElementById("newPassword").value;
    var cfm = document.getElementById("confirmPassword").value;

    if (pwd !== cfm) {
        alert("Passwords do not match!");
        document.getElementById("confirmPassword").focus();
        return false;
    }

    var result = validatePasswordStrength(pwd);
    if (!result.valid) {
        alert("Password does not meet requirements:\n- " + result.errors.join("\n- "));
        document.getElementById("newPassword").focus();
        return false;
    }

    return true;
}

function validateEditUserForm() {
    var fullName = document.getElementById("fullName").value.trim();
    if (fullName.length === 0) {
        alert("Full name is required.");
        document.getElementById("fullName").focus();
        return false;
    }

    var email = document.getElementById("email").value.trim();
    if (email.length > 0) {
        var pattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
        if (!pattern.test(email)) {
            alert("Please enter a valid email address.");
            document.getElementById("email").focus();
            return false;
        }
    }

    return true;
}

function confirmDeleteUser(username, userId) {
    if (confirm("Are you sure you want to permanently delete user '" + username + "'?\n\nThis action cannot be undone.")) {
        document.getElementById("deleteForm-" + userId).submit();
    }
}

function confirmToggleStatus(username, isActive, userId) {
    var action = isActive ? "deactivate" : "activate";
    if (confirm("Are you sure you want to " + action + " user '" + username + "'?")) {
        document.getElementById("toggleForm-" + userId).submit();
    }
}

function setupTableSearch(inputId, tableId) {
    var input = document.getElementById(inputId);
    if (!input) return;

    input.addEventListener("keyup", function () {
        var filter = this.value.toLowerCase();
        var table = document.getElementById(tableId);
        if (!table) return;

        var rows = table.querySelectorAll("tbody tr");
        var visibleCount = 0;

        for (var i = 0; i < rows.length; i++) {
            var text = rows[i].textContent.toLowerCase();
            if (text.indexOf(filter) > -1) {
                rows[i].style.display = "";
                visibleCount++;
            } else {
                rows[i].style.display = "none";
            }
        }

        var noResults = document.getElementById("noSearchResults");
        if (noResults) {
            noResults.style.display = (visibleCount === 0 && filter.length > 0) ? "block" : "none";
        }
    });
}

document.addEventListener("DOMContentLoaded", function () {
    setupTableSearch("userSearchInput", "usersTable");

    var pwdField = document.getElementById("password") || document.getElementById("newPassword");
    var strengthBar = document.getElementById("passwordStrengthBar");

    if (pwdField && strengthBar) {
        pwdField.addEventListener("input", function () {
            showPasswordStrength(this, "passwordStrengthBar");
        });
    }

    var confirmField = document.getElementById("confirmPassword");
    if (confirmField) {
        var pwdId = document.getElementById("password") ? "password" : "newPassword";
        confirmField.addEventListener("input", function () {
            checkPasswordMatch(pwdId, "confirmPassword", "passwordMatchMsg");
        });
    }
});

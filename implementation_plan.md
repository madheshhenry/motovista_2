# Refactor Admin Registration & Approval Workflow

This plan outlines the steps to remove the Master Key dependency from the Admin Registration process and replace it with a robust email-verification and Master Admin approval flow.

## User Review Required

Please review the proposed changes below. The workflow will function as follows:
1. **Registration:** New admins will enter only Name, Email, and Password.
2. **Verification:** An OTP is sent immediately to the *new admin's* email to verify it's theirs. They enter the OTP on the same screen.
3. **Approval Request:** Once the email is verified, an email is automatically sent to the Master Admin requesting approval. The app shows "Request sent".
4. **Approval Action:** The Master Admin approves the request (via a backend call or future UI).
5. **Notification:** An email is automatically sent back to the new admin notifying them they have been approved and can now log in.

> [!IMPORTANT]
> Because you are testing locally, the emails (OTP and Notifications) will be sent using your existing Google Script Mail Bridge. Please ensure your Mail Bridge is active. Also, since I will re-enable the `is_approved` lock in the backend, you will need to manually call `approve_admin.php?id=[ADMIN_ID]` in your browser to approve new admins until we build an in-app approval UI.

## Proposed Changes

---

### Database Changes
#### [MODIFY] Local Database Schema
- Execute an `ALTER TABLE` to add `is_email_verified` (INT DEFAULT 0) to the `admins` table to track the two-step process (email verified vs. master approved).

---

### Backend API
#### [MODIFY] [admin_register.php](file:///d:/motovista_deep/backend/api/motovista_backend/api/admin_register.php)
- Remove `master_key` requirement.
- Insert the admin with `is_email_verified = 0` and `is_approved = 0`.
- Generate a 6-digit OTP and save it.
- Use `USE_MAIL_BRIDGE` to send the OTP email to the *new admin*.

#### [NEW] [admin_register_verify.php](file:///d:/motovista_deep/backend/api/motovista_backend/api/admin_register_verify.php)
- Endpoint to accept `email` and `otp`.
- Verifies the OTP. If valid, updates `is_email_verified = 1`.
- Sends the "New Admin Approval Request" email to the `MASTER_EMAIL`.

#### [MODIFY] [approve_admin.php](file:///d:/motovista_deep/backend/api/motovista_backend/api/approve_admin.php)
- Fetch the admin's email address using their ID.
- Update `is_approved = 1` (or `0` for rejection).
- Use `USE_MAIL_BRIDGE` to send an email to the newly approved/rejected admin notifying them of the decision.

#### [MODIFY] [admin_verify_otp.php](file:///d:/motovista_deep/backend/api/motovista_backend/api/admin_verify_otp.php)
- Re-enable the `is_approved != 1` check that was previously commented out, enforcing the approval logic for all future logins.

---

### Android Frontend
#### [MODIFY] [activity_admin_register.xml](file:///d:/motovista_deep/frontend/app/src/main/res/layout/activity_admin_register.xml)
- Remove the Master Key `EditText` and its container.
- Add a new hidden `layout_otp` container with an OTP `EditText`.

#### [MODIFY] [AdminRegisterActivity.java](file:///d:/motovista_deep/frontend/app/src/main/java/com/example/motovista_deep/AdminRegisterActivity.java)
- Remove Master Key logic.
- Update `register()` to send the initial request without the key.
- On success, switch UI state: hide input fields, show OTP field, and change button to "Verify & Submit Request".
- Add `verifyOtp()` method to call the new `admin_register_verify.php` endpoint.
- On successful verification, show a "Request Sent" dialog and close the screen.

#### [MODIFY] [ApiService.java](file:///d:/motovista_deep/frontend/app/src/main/java/com/example/motovista_deep/api/ApiService.java)
- Add the Retrofit method for `admin_register_verify.php`.

## Verification Plan

### Automated Tests
1. Run SQL `ALTER TABLE` to update the local DB.
2. Compile and run the Android app to ensure the UI updates render correctly without crashes.

### Manual Verification
1. User attempts to register a new admin without a master key.
2. User receives an OTP email via Mail Bridge and enters it in the app.
3. Master Admin receives a "Request Approval" email.
4. User (acting as Master Admin) accesses `approve_admin.php` to approve the request.
5. New Admin receives an "Approved" email.
6. New Admin successfully logs in.

import { test, expect} from '@playwright/test';
import { waitForResetCode } from '../helpers/mailbox.js';
import { time } from 'node:console';


test('login with valid credentials', async ({page}) => {
    await page.goto('https://dashboard.ilivestock.co.uk/login');
    await expect(page).toHaveURL('https://dashboard.ilivestock.co.uk/login');
    await page.fill('input[name="username"]', 'steven@ilivestock.co.uk');
    await page.fill('input[name="password"]', 'password');
    await page.click('button[type="submit"]');
    await expect(page).toHaveURL('https://dashboard.ilivestock.co.uk/synchronizing');
    await expect(page.getByText('Synchronization in progress')).toBeVisible({timeout: 20000});
    await expect(page.getByRole('heading', {name: /My Farm/i})).toBeVisible({timeout: 20000 });
    await expect(page).toHaveURL('https://dashboard.ilivestock.co.uk/overview');
});

test('login with invalid credentials', async ({page}) => {
    await page.goto('https://feature-ilc-37-integration.d29utjx8um7ttt.amplifyapp.com/login');
    await page.fill('input[name="username"]', 'steven+chris@ilivestock.com');
    await page.fill('input[name="password"]', 'wrongpassword');
    await page.click('button[type="submit"]');
    await expect(page.getByText(/Incorrect username or password/i)).toBeVisible();
});

test('forgot password link', async ({page}) => {
    // const emailAddress = process.env.RESET_EMAIL;
    // const emailPassword = process.env.RESET_EMAIL_PASSWORD;
    // const appUsername = process.env.RESET_APP_USERNAME;
    // const oldPassword = process.env.RESET_OLD_PASSWORD;
    // const newPassword = process.env.RESET_NEW_PASSWORD;

//     if (!emailAddress || !emailPassword || !appUsername || !newPassword) {
//         throw new Error('Missing required environment variables for forgot-password test');
//   }

    await page.goto('https://dashboard-staging.ilivestock.co.uk/login');
    await page.click('text=Reset your password');
    await expect(page.getByRole('heading', {name: /Reset your password/i})).toBeVisible();
    await page.fill('input[name="username"]', 'steven+chris@ilivestock.co.uk');
    await page.click('button[type="submit"]');
    // await expect(page.getByRole('heading', {name: /Reset Password/i})).toBeVisible();
    // const code = await waitForResetCode({
    //     host: process.env.IMAP_HOST,
    //     port: Number(process.env.IMAP_PORT || 993),
    //     secure: true,
    //     user: emailAddress,
    //     password: emailPassword,
    //     subjectIncludes: 'password', // adjust to your real subject
    //     timeoutMs: 90000,
    //     pollIntervalMs: 5000,
    // });
    
    // await page.getByRole('textbox', { name: /verification code/i }).fill(code);
    // await page.getByLabel(/new password/i).fill(newPassword);
    // await page.getByLabel(/confirm password|confirm new password/i).fill(newPassword);
    // await page.click('button[type="submit"]');
    await expect(page.locator('input[name="confirmation_code"]')).toBeVisible();
});

test('login with empty fields', async ({page}) => {
    await page.goto('https://dashboard-staging.ilivestock.co.uk/login');
    const emailInput = page.locator('input[name="username"]');
    const passwordInput = page.locator('input[name="password"]');
    await page.click('button[type="submit"]');
    await expect(emailInput).toHaveJSProperty('validationMessage', 'Please fill out this field.');
    await expect(passwordInput).toHaveJSProperty('validationMessage', 'Please fill out this field.');
    
    const emailValidation = await emailInput.evaluate(el => el.validity.valueMissing);
    const passwordValidation = await passwordInput.evaluate(el => el.validity.valueMissing);
    expect(emailValidation).toBe(true);
    expect(passwordValidation).toBe(true);
    

});

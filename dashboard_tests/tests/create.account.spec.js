import{ test, expect } from '@playwright/test';
// import readline from 'readline';

// function waitForInput(promptText) {
//   const rl = readline.createInterface({
//     input: process.stdin,
//     output: process.stdout,
//   });

//   return new Promise(resolve => {
//     rl.question(promptText, answer => {
//       rl.close();
//       resolve(answer);
//     });
//   });
// }
import fs from 'fs';

async function waitForVerificationCode(filePath, timeoutMs = 180000, pollMs = 1000) {
  const start = Date.now();

  while (Date.now() - start < timeoutMs) {
    console.log('Polling for verification code...');

    if (fs.existsSync(filePath)) {
      const code = fs.readFileSync(filePath, 'utf8').trim();
      console.log(`File exists. Current contents: "${code}"`);

      if (code) {
        fs.writeFileSync(filePath, '');
        return code;
      }
    }

    await new Promise(resolve => setTimeout(resolve, pollMs));
  }

  throw new Error('Timed out waiting for verification code in file');
}

test.only('create new UK test account', async ({page}) => {
    test.setTimeout(180000);
    await page.goto('https://dashboard-staging.ilivestock.co.uk/registration');
    await expect(page.getByRole('heading', {name: /Create an iLivestock account/i})).toBeVisible();
    const random = Math.floor(Math.random() * 100000);
    const email = `steven+test${random}@ilivestock.co.uk`;
    await page.getByLabel('E-mail address').fill(email);
    await page.getByLabel(/^Password$/).fill('password');
    await page.getByLabel('Confirm Password').fill('password');
    await page.getByRole('checkbox', { name: /Accept Privacy Policy & Terms and Conditions/i }).check();
    await page.click('button[type="submit"]');

    await expect(page.getByRole('heading', {name: /Verify your account/i})).toBeVisible();
    const verificationCode = await waitForVerificationCode('./verification-code.txt');
    
    await page.getByLabel('Verification code').fill(verificationCode);
    await page.click('button[type="submit"]');

    await expect(page.getByRole('heading', {name: /Account Details/i})).toBeVisible({ timeout: 15000 });
    await page.getByLabel('Given Name').fill('Steven');
    await page.getByLabel('Family Name').fill('Segaud');
    // 1. Focus the dropdown input
    await page.locator('#location').fill('United');
    // 2. Select the option from dropdown
    await page.getByText('(UK) United Kingdom').click();
    await page.getByLabel('Phone Number').fill('07934108770');
    await page.click('button[type="submit"]');

    const frame = page.frameLocator('iframe');
    const startTrialButton = frame.getByRole('button', { name: /start trial/i });
    await expect(startTrialButton).toBeVisible({ timeout: 15000 });
    await startTrialButton.click();

    await expect(page).toHaveURL(/checkout\.stripe\.com/);
    const emailDisplay = page.locator('.ReadOnlyFormField-title');
    await expect(emailDisplay).toHaveText(email, { timeout: 10000 });
    await expect(page.getByRole('heading', {name: /Try iLivestock Platform/i})).toBeVisible({ timeout: 15000 });
    await page.locator('[data-testid="card-accordion-item"]').click();
    await expect(page.getByLabel(/card number/i)).toBeVisible({ timeout: 10000 });
    await expect(page.getByLabel(/expiration/i)).toBeVisible({ timeout: 10000 });
    const cvcInput = page.locator('input[aria-label="CVC"]');
    await expect(cvcInput).toBeVisible({ timeout: 10000 });
    await page.getByLabel('Card number').fill('4242424242424242');
    await page.getByLabel(/expiration/i).fill('12/34');
    await cvcInput.fill('123');
    await page.getByLabel(/Cardholder Name/i).fill('Steven Segaud');
    await page.getByRole('button', {name: 'Enter address manually'}).click();
    await page.getByLabel(/Address line 1/i).fill('10 Downing Street');
    await page.getByLabel(/Town or city/i).fill('London');
    await page.getByLabel(/Postal code/i).fill('SW1A 1AA');
    await page.getByRole('button', { name: 'Start trial' }).click({ timeout: 15000 });
  
    
    await expect(page).toHaveURL(/setup_success/, { timeout: 45000 });
    await expect(page.getByText(/your invoice is in your dashboard/i)).toBeVisible({ timeout: 15000 });     

});

test('create new US test account', async ({page}) => {
    test.setTimeout(180000);
    await page.goto('https://dashboard-staging.ilivestock.co.uk/registration');
    await expect(page.getByRole('heading', {name: /Create an iLivestock account/i})).toBeVisible();
    const random = Math.floor(Math.random() * 100000);
    const email = `steven+usatest${random}@ilivestock.co.uk`;
    await page.getByLabel('E-mail address').fill(email);
    await page.getByLabel(/^Password$/).fill('password');
    await page.getByLabel('Confirm Password').fill('password');
    await page.getByRole('checkbox', { name: /Accept Privacy Policy & Terms and Conditions/i }).check();
    await page.click('button[type="submit"]');

    await expect(page.getByRole('heading', {name: /Verify your account/i})).toBeVisible();
    const verificationCode = await waitForVerificationCode('./verification-code.txt');
    
    await page.getByLabel('Verification code').fill(verificationCode);
    await page.click('button[type="submit"]');

    await expect(page.getByRole('heading', {name: /Account Details/i})).toBeVisible({ timeout: 15000 });
    await page.getByLabel('Given Name').fill('Steven');
    await page.getByLabel('Family Name').fill('Segaud');
    // 1. Focus the dropdown input
    await page.locator('#location').fill('United');
    // 2. Select the option from dropdown
    await page.getByText('(US) United States').click();
    await page.getByLabel('Phone Number').fill('3074103456');
    await page.click('button[type="submit"]');
    
    const frame = page.frameLocator('iframe');
    const startTrialButton = frame.getByRole('button', { name: /start trial/i });
    const priceDisplay = frame.locator('.CurrencyAmount');
    await expect(priceDisplay).toHaveText('$400', { timeout: 10000 });
    await expect(startTrialButton).toBeVisible({ timeout: 15000 });
    await startTrialButton.click();

    await expect(page).toHaveURL(/checkout\.stripe\.com/);
    const emailDisplay = page.locator('.ReadOnlyFormField-title');
    await expect(emailDisplay).toHaveText(email, { timeout: 10000 });
    await expect(page.getByRole('heading', {name: /Try iLivestock Platform/i})).toBeVisible({ timeout: 15000 });
    await expect(page.getByText(/tax/i)).not.toBeVisible(); // US checkout should not show tax
    await page.locator('#billingName').fill('Steven Segaud');
    await page.getByRole('button', {name: 'Enter address manually'}).click();
    await page.getByLabel(/Address line 1/i).fill('1600 Pennsylvania Avenue NW');
    await page.getByLabel(/Town or city/i).fill('Washington');
    await page.getByLabel(/Postal code/i).fill('20500');
    await page.getByRole('button', { name: 'Start trial' }).click();

    await expect(page).toHaveURL(/setup_success/, { timeout: 45000 });
    await expect(page.getByText(/your invoice is in your dashboard/i)).toBeVisible({ timeout: 15000 });

});

test('create new Paraguay test account', async ({page}) => {
    test.setTimeout(180000);
    await page.goto('https://dashboard-staging.ilivestock.co.uk/registration');
    await expect(page.getByRole('heading', {name: /Create an iLivestock account/i})).toBeVisible();
    const random = Math.floor(Math.random() * 100000);
    const email = `steven+paraguaytest${random}@ilivestock.co.uk`;
});
import {test, expect} from '@playwright/test';

test('homepage loads and shows main navigation', async ({page}) => {
    await page.goto('https://automationexercise.com/');
    await expect(page).toHaveTitle(/Automation Exercise/);
    await expect(page.getByRole('link', {name: /products/i})).toBeVisible();
    await expect(page.getByRole('link', {name: /signup\s*\/\s*login/i})).toBeVisible();
});

test('search for a product and verify results', async ({page}) => {
    await page.goto('https://automationexercise.com/products');
    await page.fill('#search_product', 'dress');
    await page.click('#submit_search');
    await expect(page.getByText(/searched products/i)).toBeVisible();
    const productTitles = page.locator('.productinfo.text-center p');
    await expect(productTitles.first()).toBeVisible();
    await expect(productTitles.first()).toContainText(/dress/i);
}); 
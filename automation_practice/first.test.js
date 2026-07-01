const { test, expect } = require('@playwright/test');
const { acceptConsentIfVisible } = require('../helpers/consentHelper');

test('homepage loads and shows main navigation', async ({page}) => {
    await page.goto('https://automationexercise.com/');
    await expect(page).toHaveTitle(/Automation Exercise/);
    await expect(page.getByRole('link', {name: /products/i})).toBeVisible();
    await expect(page.getByRole('link', {name: /signup\s*\/\s*login/i})).toBeVisible();
});

test('search for a product, add to cart and verify results', async ({page}) => {
    await page.goto('https://automationexercise.com/');
    await acceptConsentIfVisible(page);
    await expect(page.getByRole('link', {name: /products/i})).toBeVisible();
    await page.getByRole('link', {name: /products/i}).click();
    await expect(page.getByRole('heading', {name: /all products/i})).toBeVisible();
    await page.fill('#search_product', 'dress');
    await page.click('#submit_search');
    await expect(page.getByText(/searched products/i)).toBeVisible();
    //give search results a variable
    const searchResults = page.locator('.features_items .product-image-wrapper');
    const firstResult = searchResults.first();
    await expect(firstResult).toBeVisible();
    // hover the first result so the "Add to cart" control becomes interactable
    await firstResult.hover();
    //give results count a variable and assert that it is greater than 0
    const resultCount = await searchResults.count();
    expect(resultCount).toBeGreaterThan(0);
    await firstResult.getByRole('link', {name: /add to cart/i}).click();
    await expect(page.getByText(/item added to cart/i)).toBeVisible();
    await page.getByRole('link', {name: /view cart/i}).click();
    await expect(page.getByRole('heading', {name: /shopping cart/i})).toBeVisible();
    await expect(page.getByText(/dress/i)).toBeVisible();
    await expect(page.getByText(/price/i)).toBeVisible();
    await expect(page.getByText(/quantity/i)).toBeVisible();
    await expect(page.getByText(/total/i)).toBeVisible();
    await expect(page.getByRole('button', {name: /proceed to checkout/i})).toBeVisible();
}); 
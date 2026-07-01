async function acceptConsentIfVisible(page) {
  const consentButton = page.getByRole('button', { name: /^Consent$/ });

  try {
    await consentButton.click({ timeout: 5000 });
  } catch {
    // Consent banner was not shown, so continue the test
  }
}

module.exports = { acceptConsentIfVisible };
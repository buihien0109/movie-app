// format all currency in page with class name is currency
function formatAllCurrency() {
    const currencyElements = document.querySelectorAll('.currency');
    currencyElements.forEach(element => {
        element.innerText = formatCurrency(element.innerText);
    })
}

formatAllCurrency();
function promptAndSetName(form) {
    const input = form.querySelector('#nameUpdater');
    const currentValue = input.value;

    const newName = prompt("Enter a new name for the API key:", currentValue);
    if (newName && newName.trim() !== "") {
        input.value = newName.trim();
        form.submit();
    }
}

// inputs

addEventListener("DOMContentLoaded",()=>{
    const formGroup = document.querySelectorAll(".login-form-group")

    formGroup.forEach(group => {
        const input = group.querySelector("input")
        if (input && input.type != "submit" && !group.classList.contains("login-form-options")) {
            const label = group.querySelector("label")
            if (input.value != "") label.className = "label-active";
            input.addEventListener("focus",()=>{
                label.className = "label-active";
            })
            input.addEventListener("blur",(event)=>{
                console.log(event.target.value);
                if(event.target.value == "") {
                    label.classList.remove("label-active");
                }
            })
        }
    })
})
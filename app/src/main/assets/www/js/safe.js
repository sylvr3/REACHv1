(function(){
    const currentProgress = {
        S: {
            S1: true,
            S2: false
        },
        A: false,
        F: false,
        E: false
    };

    const S2Text = "SPEAK YOUR MIND";

    const situation  = {
        description: safe.getSituationDescription(),
        rightAnswer: safe.getSituationRightAnswer(),
        wrongAnswer1: safe.getSituationWrongAnswer1(),
        wrongAnswer2: safe.getSituationWrongAnswer2()
    }

    const mainCardText = document.getElementById("main-card-text");
    const singleDiv = document.getElementById("single");
    const quadDiv = document.getElementById("quad");
    const leftImg = document.getElementById("left-img");
    const rightImg = document.getElementById("right-img");

    leftImg.addEventListener("click", backScreenEventHandler);
    rightImg.addEventListener("click", nextScreenEventHandler);

    mainCardText.innerHTML = situation.description;

    function backScreenEventHandler(){
        if(currentProgress.S.S1){
            return safe.goToHomeScreen();
        }else if(currentProgress.S.S2){
            mainCardText.innerHTML = situation.description;
        }else if(currentProgress.A){
            singleDiv.classList.remove("hide");
            quadDiv.classList.add("hide");
            rightImg.classList.remove("hide");
        }else if(currentProgress.F){

        }else if(currentProgress.E){

        }
        _updateProgress();
        _updateLeftButton();
    }

    function nextScreenEventHandler(){
        if(currentProgress.S.S1){
            mainCardText.innerHTML = S2Text;
        }else if(currentProgress.S.S2){
            singleDiv.classList.add("hide");
            quadDiv.classList.remove("hide");
            rightImg.classList.add("hide");
        }else if(currentProgress.A){

        }else if(currentProgress.F){

        }else if(currentProgress.E){

        }
        _updateProgress(true);
        _updateLeftButton();
    }

    function _updateLeftButton(){
        if(currentProgress.S.S1){
            leftImg.src = "file:///android_res/drawable/home_down.png";
        }else{
            leftImg.src = "file:///android_res/drawable/back_triangle_down.png";
        }
    }

    function _updateProgress(moveForward){
        if(currentProgress.S.S1){
            currentProgress.S.S1 = false;
            currentProgress.S.S2 = moveForward ? true : false;
        }else if(currentProgress.S.S2){
            currentProgress.S.S2 = false;
            currentProgress.A = moveForward ? true : false;
            currentProgress.S.S1 = moveForward ? false : true;
        }else if(currentProgress.A){
            currentProgress.A = false;
            currentProgress.F = moveForward ? true : false;
            currentProgress.S.S2 = moveForward ? false : true;
        }else if (currentProgress.F){
            currentProgress.F = false;
            currentProgress.E = moveForward ? true : false;
            currentProgress.A = moveForward ? false : true;
        } else if (currentProgress.E) {
            currentProgress.E = false;
            currentProgress.F = moveForward ? false : true;
        }
    }
})();
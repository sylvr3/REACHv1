(function(){
    //arch
    var initScreen = document.getElementById("init-screen");
    var abmtActivityScreen = document.getElementById("abmt-activity");
    var endScreen = document.getElementById("end-screen");

    //init div
    var tutorialButton = document.getElementById("tutorial-button");
    var abmtButton = document.getElementById("activity-button");

    //abmt div
    var topImg = document.getElementById("top-img");
    var botImg = document.getElementById("bot-img");
    var plusSign = document.getElementById("plus-sign");
    var leftButton = document.getElementById("left-button");
    var rightButton = document.getElementById("right-button");
    var rightArrowSource = "file:///android_res/drawable/right.bmp";
    var leftArrowSource = "file:///android_res/drawable/left.bmp";

    //end div
    var correctAnswerParagraph = document.getElementById("correct-answer-count");
    var speedParagraph = document.getElementById("speed");

    var IMAGETIME = 500, PLUSTIME=200, ANSWERTIME, ANSWERING_ENABLED = false, ITERATIONS, OVERTIME = false, LEFT_CORRECT, RIGHT_CORRECT, COUNT, CORRECT_COUNT;
    var timeToAnswer;
    var speedArray = [];

    leftButton.addEventListener("click", answerClickHandler.bind(leftButton));
    rightButton.addEventListener("click", answerClickHandler.bind(rightButton));
    tutorialButton.addEventListener("click", tutorialButtonClickHandler);
    abmtButton.addEventListener("click", abmtButtonClickHandler);

    function abmtButtonClickHandler(){
        abmt.setTrialVars();
        ANSWERTIME = 200;//MS
        ITERATIONS = 160;
        CORRECT_COUNT = 0;
        begin();
    }

    function tutorialButtonClickHandler(){
        abmt.setTutorialVars();
        ANSWERTIME = 1000;//ms
        ITERATIONS = 40;
        CORRECT_COUNT = 0;
        begin();
    }

    function answerClickHandler(){
        if(!ANSWERING_ENABLED) return;
        if(COUNT < ITERATIONS){
            if(!OVERTIME && (RIGHT_CORRECT && this === rightButton || LEFT_CORRECT && this === leftButton)){
                abmt.ding();
                CORRECT_COUNT += 1;
            }
            speedArray.push(timeToAnswer - new Date());
            getNewImage();
            COUNT += 1;
        }else{
            var total = 0;
            speedArray.forEach(function(item){
                total += item;
            });

            correctAnswerParagraph.innerHTML = "Correct Answers: " + CORRECT_COUNT;
            speedParagraph.innerHTML = "Speed score: " + total/ speedArray.size();
            initScreen.classList.add("hide");
            abmtActivityScreen.classList.add("hide");
            endScreen.classList.remove("hide");
        }
        ANSWERING_ENABLED = false;
    }

    function begin(){
        COUNT = 0;
        initScreen.classList.add("hide");
        abmtActivityScreen.classList.remove("hide");
        getNewImage();
    }

    function restart(){
        COUNT = 0;
        abmtActivityScreen.classList.add("hide");
        initScreen.classList.remove("hide");
        getNewImage();
    }

    function moveToPlusScreen(imgTop, imgBot){
        if(move){

        }else {
            topImg.src = "";
            botImg.src = "";
        }
    }

    function showPlusSign(show){
        if(show){
            plusSign.classList.remove("hide");
        }else{
            plusSign.classList.remove("hide");
        }
    }

    function getNewImage(){
        topImg.src = "";
        botImg.src = "";
        showPlusSign(true);

        window.setTimeout(
            var image = abmt.getImage();
            var imageArr = image.split("||");
            var image1 = imageArr[0];
            var image2 = imageArr[1];

            if(imageArr[2] === "left"){
                LEFT_CORRECT = true;
                RIGHT_CORRECT = false;
            }else {
                RIGHT_CORRECT = true;
                LEFT_CORRECT = false;
            }

            topImg.src = image1;
            botImg.src = image2;
            showPlusSign(false);

            timeToAnswer = new Date();

            window.setTimeout(function(){
                if(RIGHT_CORRECT){
                    topImg.src = "";
                    botImg.src = rightArrowSource;
                }else{
                    botImg.src = "";
                    topImg.src = leftArrowSource;
                }
                ANSWERING_ENABLED = true;
                window.setTimeout(function (){
                    if(!OVERTIME){
                        OVERTIME = true;
                    }
                }, ANSWERTIME);
            },IMAGETIME);
        , PLUSTIME);
        OVERTIME = false;
    }
})();
(function(){
    const currentProgress = {
        S: {
            S1: true,
            S2: false
        },
        A: false,
        F: {
            F1: false,
            F2: false
        },
        E: false,
        WrongMessageScreen: false
    };

    const FirmButKindVoiceText = "FIRM BUT KIND VOICE";
    const WrongAnswerText = "TRY AGAIN!";

    const situation  = {
        description: safe.getSituationDescription(),
        rightAnswer: safe.getSituationRightAnswer(),
        wrongAnswer1: safe.getSituationWrongAnswer1(),
        wrongAnswer2: safe.getSituationWrongAnswer2()
    }

    var isRecording = false;

    const mainCardImg = document.getElementById("main-card-img");
    const mainCardText = document.getElementById("main-card-text");
    const singleDiv = document.getElementById("single");
    const quadDiv = document.getElementById("quad");
    const topImg = document.getElementById("top-img");
    const leftImg = document.getElementById("left-img");
    const rightImg = document.getElementById("right-img");
    const recordImg = document.getElementById("safe-record-img");
    const a1 = document.getElementById("a1");
    const a2 = document.getElementById("a2");
    const a3 = document.getElementById("a3");
    const a1Text = document.getElementById("a1-text");
    const a2Text = document.getElementById("a2-text");
    const a3Text = document.getElementById("a3-text");
    const s_progress = document.getElementById("progress-s");
    const a_progress = document.getElementById("progress-a");
    const f_progress = document.getElementById("progress-f");
    const e_progress = document.getElementById("progress-e");

    leftImg.addEventListener("click", backScreenEventHandler);
    rightImg.addEventListener("click", nextScreenEventHandler);
    recordImg.addEventListener("click", recordEventHandler);
    a1.addEventListener("click", askNicelyEventHandler.bind(a1));
    a2.addEventListener("click", askNicelyEventHandler.bind(a2));
    a3.addEventListener("click", askNicelyEventHandler.bind(a3));

    const S1Text = "SITUATION: <br><br>" + situation.description;
    const S2Text = "SPEAK YOUR MIND";

    mainCardText.innerHTML = S1Text;

    function backScreenEventHandler(){
        if(currentProgress.WrongMessageScreen){
            singleDiv.classList.add("hide");
            quadDiv.classList.remove("hide");
        } else if(currentProgress.S.S1){
            return safe.goToHomeScreen();
        }else if(currentProgress.S.S2){
            mainCardText.innerHTML = S1Text;
        }else if(currentProgress.A){
            singleDiv.classList.remove("hide");
            quadDiv.classList.add("hide");
            rightImg.classList.remove("hide");
            mainCardText.innerHTML = S2Text;
            topImg.classList.add("hide");
            a_progress.src = "file:///android_res/drawable/a_white.png";
            s_progress.src = "file:///android_res/drawable/s_yellow.png";
        }else if(currentProgress.F.F1){
            singleDiv.classList.add("hide");
            quadDiv.classList.remove("hide");
            rightImg.classList.add("hide");
            topImg.classList.remove("hide");
            topImg.src = "file:///android_res/drawable/ask_nicely.png";
            a_progress.src = "file:///android_res/drawable/a_yellow.png";
            f_progress.src = "file:///android_res/drawable/f_white.png";
        } else if (currentProgress.F.F2){
            mainCardImg.src = "file:///android_res/drawable/wh_card_down.png";
            mainCardText.classList.remove("hide");
            recordImg.classList.add("hide");
            rightImg.classList.remove("hide");
            topImg.classList.add("hide");
        }else if(currentProgress.E){

        }
        _updateProgress();
        _updateLeftButton();
    }

    function nextScreenEventHandler(){
        if(currentProgress.WrongMessageScreen){
            mainCardText.innerHTML = WrongAnswerText;
            singleDiv.classList.remove("hide");
            quadDiv.classList.add("hide");
        } else if(currentProgress.S.S1){
            mainCardText.innerHTML = S2Text;
        }else if(currentProgress.S.S2){
            singleDiv.classList.add("hide");
            quadDiv.classList.remove("hide");
            rightImg.classList.add("hide");
            createAskNicelyOptions();
            topImg.classList.remove("hide");
            s_progress.src = "file:///android_res/drawable/s_white.png";
            a_progress.src = "file:///android_res/drawable/a_yellow.png";
        }else if(currentProgress.A){
            mainCardText.innerHTML = FirmButKindVoiceText;
            singleDiv.classList.remove("hide");
            quadDiv.classList.add("hide");
            rightImg.classList.remove("hide");
            topImg.classList.remove("hide");
            a_progress.src = "file:///android_res/drawable/a_white.png";
            f_progress.src = "file:///android_res/drawable/f_yellow.png";
        }else if(currentProgress.F.F1){
            mainCardImg.src = "file:///android_res/drawable/safe_blob.png";
            topImg.src = "file:///android_res/drawable/safe_msg_eye_contact.png";
            mainCardText.classList.add("hide");
            recordImg.classList.remove("hide");
            rightImg.classList.add("hide");
            topImg.classList.remove("hide");
        }else if(currentProgress.F.F2){
            topImg.src = "file:///android_res/drawable/gj_title.png";
            mainCardText.classList.add("hide");
        }else if(currentProgress.E){
            topImg.classList.add("hide");
            mainCardImg.src = "file:///android_res/drawable/wh_card_down.png";
        }

        _updateProgress(true);
        _updateLeftButton();
    }

    function askNicelyEventHandler(){
        if(!this.isAnswer){
            currentProgress.WrongMessageScreen = true;
        }
        nextScreenEventHandler();
    }

    function recordEventHandler(){
        if(isRecording){
            safe.stopRecording();
            isRecording = false;
            leftImg.classList.remove("hide");
            recordImg.classList.add("hide");
            rightImg.classList.remove("hide");
            nextScreenEventHandler();
        }else if(isRecording === false && recordImg.src === "file:///android_res/drawable/done_up.png"){
            topImg.src = "file:///android_res/drawable/safe_msg_push_record.png";
            recordImg.src = "file:///android_res/drawable/safe_record.png";
        }else{
            safe.startRecording();
            recordImg.src = "file:///android_res/drawable/done_up.png";
            isRecording = true;
            leftImg.classList.add("hide");
            hideProgress(true);
        }
    }

    function createAskNicelyOptions(){
        var answerIndex = Math.ceil(Math.random() * 3);
        if(answerIndex === 1){
            a1.isAnswer = true;
            a2.isAnswer = false;
            a3.isAnswer = false;
            a1Text.innerHTML = situation.rightAnswer;
            a2Text.innerHTML = situation.wrongAnswer1;
            a3Text.innerHTML = situation.wrongAnswer2;
        }else if(answerIndex === 2){
            a1.isAnswer = false;
            a2.isAnswer = true;
            a3.isAnswer = false;
            a1Text.innerHTML = situation.wrongAnswer1;
            a2Text.innerHTML = situation.rightAnswer;
            a3Text.innerHTML = situation.wrongAnswer2;
        }else{
            a1.isAnswer = false;
            a2.isAnswer = false;
            a3.isAnswer = true;
            a1Text.innerHTML = situation.wrongAnswer1;
            a2Text.innerHTML = situation.wrongAnswer2;
            a3Text.innerHTML = situation.rightAnswer;
        }
    }

    function hideProgress(hide){
        if(hide){
            s_progress.classList.add("hide");
            a_progress.classList.add("hide");
            f_progress.classList.add("hide");
            e_progress.classList.add("hide");
        }else{
            s_progress.classList.remove("hide");
            a_progress.classList.remove("hide");
            f_progress.classList.remove("hide");
            e_progress.classList.remove("hide");
        }
    }

    function _updateLeftButton(){
        if(currentProgress.S.S1){
            leftImg.src = "file:///android_res/drawable/home_down.png";
        } else if(currentProgress.E){
            leftImg.src = "file:///android_res/drawable/home_down.png";
        }else{
            leftImg.src = "file:///android_res/drawable/back_triangle_down.png";
        }
    }

    function _updateProgress(moveForward){
        if(currentProgress.WrongMessageScreen){
            currentProgress.WrongMessageScreen = moveForward ? true : false;
        } else if(currentProgress.S.S1){
            currentProgress.S.S1 = false;
            currentProgress.S.S2 = moveForward ? true : false;
        }else if(currentProgress.S.S2){
            currentProgress.S.S2 = false;
            currentProgress.A = moveForward ? true : false;
            currentProgress.S.S1 = moveForward ? false : true;
        }else if(currentProgress.A){
            currentProgress.A = false;
            currentProgress.F.F1 = moveForward ? true : false;
            currentProgress.S.S2 = moveForward ? false : true;
        }else if (currentProgress.F.F1){
            currentProgress.F.F1 = false;
            currentProgress.F.F2 = moveForward ? true : false;
            currentProgress.A = moveForward ? false : true;
        } else if (currentProgress.F.F2) {
            currentProgress.F.F2 = false;
            currentProgress.E = moveForward ? true : false;
            currentProgress.F.F1 = moveForward ? false : true;
        } else if (currentProgress.E) {
            if(moveForward){
                currentProgress.E = false;
                currentProgress.S.S2 = true;
                backScreenEventHandler();
            }else{
                safe.goToHomeScreen(true);
            }
        }
    }
})();
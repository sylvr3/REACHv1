(function(){
    var topImg = document.getElementById("top-img");
    var botImg = document.getElementById("bot-img");
    var leftButton = document.getElementById("left-button");
    var rightButton = document.getElementById("right-button");
    var rightArrowSource = "file:///android_res/drawable/right.bmp";
    var leftArrowSource = "file:///android_res/drawable/left.bmp";

    leftButton.addEventListener("click", clickHandler());
    rightButton.addEventListener("click", clickHandler());

    function clickHandler(){
        getNewImage();
    }

    function getNewImage(){
        var image = abmt.getImage();
        var imageArr = image.split("||");
        var image1 = imageArr[0];
        var image2 = imageArr[1];

        topImg.src = image1;
        botImg.src = image2;
        setTimeout(showArrows, 700);
    }

    function showArrows(){
//        topImg.src = rightArrowSource;
//        botImg.classList.add("hide");
    }

    getNewImage();
})();

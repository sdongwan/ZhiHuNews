function onLoadHtml(){

 var allImg=document.querySelectorAll("img");
 allImage=Array.prototype.slice.call(allImg,0);
 allImage.forEach(function(image) {
 ZhihuNews.loadImage(image.getAttribute("newssrc"));

 	});

 }

 function onImageLoadComplete(pOldUrl,pNewUrl){
var allImage = document.querySelectorAll("img");
	allImage = Array.prototype.slice.call(allImage, 0);
	allImage.forEach(function(image) {
		if (image.getAttribute("newssrc") == pOldUrl) {
			image.src = pNewUrl;
		}
	});


 }
 function onImageClick(pImage) { // 图片点击事件

 		//ZhihuNews.openImage(pImage.getAttribute("newssrc"));
 		ZhihuNews.openImage(pImage.getAttribute("src"));

 };




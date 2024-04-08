function handelLoginForm() {
  const container = document.getElementById("container");
  container.classList.toggle("right-panel-active");
}

function SliderProductPage() {
  const swiper = new Swiper(".swiper.swiper__product-category", {
    // Optional parameters
    direction: "horizontal",
    loop: true,

    pagination: {
      el: ".swiper-pagination",
    },
    // Navigation arrows
    navigation: {
      nextEl: ".swiper-button-next",
      prevEl: ".swiper-button-prev",
    },
    autoplay: {
      delay: 5000,
      disableOnInteraction: false,
    },
  });
}

function handelNavBar() {
            var navbarSupportedContent = document.getElementById(
                "navbarSupportedContent"
            );

            var toggleButton = document.querySelector(".navbar-toggler");

            toggleButton.addEventListener("click", function () {
                var isShow = navbarSupportedContent.classList.contains("show");

                if (isShow) {
                navbarSupportedContent.classList.remove("show");
                } else {
                navbarSupportedContent.classList.add("show");
                }
            });
}

function start() {
  handelNavBar()
  SliderProductPage();
}
start();

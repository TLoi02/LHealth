function getCity() {
  const headers = new Headers();
  headers.append("token", "88a18b0c-3ff0-11ee-af43-6ead57e9219a");

  fetch("https://online-gateway.ghn.vn/shiip/public-api/master-data/province", {
    method: "GET",
    headers: headers,
  })
    .then((response) => response.json())
    .then(handelRenderCity)
    .catch((error) => {
      console.error("Error fetching data:", error);
    });
}

function handelRenderCity(city) {
  const getCity = city;
  const htmlContent = document.querySelector(".city__content");

  const htmlCode = getCity.data
    .map((city) => {
      return `
          <option value="${city.ProvinceID}">${city.ProvinceName}</option>
        `;
    })
    .join("");

  htmlContent.innerHTML = htmlCode;
}

function handelCitySelect() {
  const selectElement = document.querySelector(".city__content");

  selectElement.addEventListener("change", function () {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    const selectedCity = selectedOption.value;
    getDistrict(selectedCity);
  });
}

function getDistrict(idCity) {
  const headers = new Headers();
  headers.append("token", "88a18b0c-3ff0-11ee-af43-6ead57e9219a");
  const url = `https://online-gateway.ghn.vn/shiip/public-api/master-data/district?province_id=${idCity}`;

  fetch(url, {
    method: "GET",
    headers: headers,
  })
    .then((response) => response.json())
    .then(handelRenderDistrict)
    .catch((error) => {
      console.error("Error fetching data:", error);
    });
}

function handelRenderDistrict(district) {
  const getDistrict = district;
  const htmlContent = document.querySelector(".district__content");

  const htmlCode = getDistrict.data
    .map((district) => {
      return `
          <option value="${district.DistrictID}">${district.DistrictName}</option>
        `;
    })
    .join("");

  htmlContent.innerHTML = htmlCode;
}

function handelWardSelect() {
  const selectElement = document.querySelector(".district__content");

  selectElement.addEventListener("change", function () {
    const selectedOption = selectElement.options[selectElement.selectedIndex];
    const selectedWard = selectedOption.value;
    getWard(selectedWard);
  });
}

function getWard(idWard) {
  const headers = new Headers();
  headers.append("token", "88a18b0c-3ff0-11ee-af43-6ead57e9219a");
  const url = `https://online-gateway.ghn.vn/shiip/public-api/master-data/ward?district_id=${idWard}`;

  fetch(url, {
    method: "GET",
    headers: headers,
  })
    .then((response) => response.json())
    .then(handelRenderWard)
    .catch((error) => {
      console.error("Error fetching data:", error);
    });
}

function handelRenderWard(ward) {
  const getWard = ward;
  const htmlContent = document.querySelector(".ward__content");

  const htmlCode = getWard.data.map((ward) => {
      return `
        <option value="${ward.WardCode}">${ward.WardName}</option>
        `;
    })
    .join("");

  htmlContent.innerHTML = htmlCode;
}

function start() {
  getCity();
  handelCitySelect();
  handelWardSelect();
}
start();

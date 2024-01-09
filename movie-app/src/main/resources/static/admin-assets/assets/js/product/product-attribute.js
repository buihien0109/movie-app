// --------- Product Attribute ---------
let attributeId = null;
const btnOpenModalCreateAttribute = document.getElementById('btn-open-modal-create-attribute');
btnOpenModalCreateAttribute.addEventListener('click', function () {
    const modalTitle = document.getElementById('modal-attribute-title');
    const inputAttributeName = document.getElementById('input-attribute-name');
    modalTitle.innerText = "Thêm thuộc tính";
    inputAttributeName.value = "";
    $('#modal-attribute').modal('show');
});

function openModalUpdateAttribute(id) {
    // find attribute by id
    console.log("product :", product);
    const attribute = product.attributes.find(attribute => attribute.attributeId == id);
    console.log(attribute)

    const modalTitle = document.getElementById('modal-attribute-title');
    const inputAttributeName = document.getElementById('input-attribute-name');
    modalTitle.innerText = "Cập nhật thuộc tính";
    inputAttributeName.value = attribute.attributeName;
    $('#modal-attribute').modal('show');
    attributeId = id;
}

$('#modal-attribute').on('hidden.bs.modal', function (event) {
    attributeId = null;
    const inputAttributeName = document.getElementById('input-attribute-name');
    inputAttributeName.value = "";
})

// Handle create attribute send request to server
const createAttribute = () => {
    const attributeName = document.getElementById('input-attribute-name').value;
    const attribute = {
        attributeName
    }

    axios.post(`/api/v1/admin/products/${product.productId}/attributes`, attribute)
        .then(response => {
            console.log(response);
            if (response.status === 200) {
                toastr.success("Thêm thuộc tính thành công");

                // add attribute to UI
                const productAttributeList = document.getElementById('product-attribute-list');
                productAttributeList.insertAdjacentHTML("beforeend", `
                            <div class="col-6" data-attribute-id="${response.data.attributeId}">
                                <div class="product-attribute-item mb-3">
                                    <div class="d-flex align-items-center mb-2">
                                        <label class="mb-0">${response.data.attributeName}</label>
                                        <div class="ml-2">
                                            <span class="btn-action btn-action-edit" onclick="openModalUpdateAttribute(${response.data.attributeId})"><i class="fas fa-pencil-alt"></i></span>
                                            <span class="btn-action btn-action-delete" onclick="deleteAttribute(${response.data.attributeId})"><i class="fas fa-trash-alt"></i></span>
                                        </div>
                                    </div>
                                    <textarea class="form-control" rows="5" onkeyup="saveAttributeValue(this, ${response.data.attributeId})">${response.data.attributeValue}</textarea>
                                </div>
                            </div>
                        `)

                // add attribute to product
                product.attributes.push(response.data);

                $("#modal-attribute").modal('hide')
            }
        })
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        })
}

const updateAttribute = (attributeId) => {
    const attributeName = document.getElementById('input-attribute-name').value;
    const attribute = {
        attributeName
    }
    console.log(attribute);

    axios.put(`/api/v1/admin/products/${product.productId}/attributes/${attributeId}`, attribute)
        .then(response => {
            console.log(response);
            if (response.status === 200) {
                toastr.success("Cập nhật thuộc tính thành công");

                // find attribute item on UI using data-attribute-id
                const productAttributeList = document.getElementById('product-attribute-list');
                const attributeItem = productAttributeList.querySelector(`[data-attribute-id="${attributeId}"]`);
                attributeItem.querySelector('label').innerText = response.data.attributeName;

                // update attribute in product
                const attribute = product.attributes.find(attribute => attribute.attributeId == attributeId);
                attribute.attributeName = response.data.attributeName;
                console.log(product)

                $("#modal-attribute").modal('hide')
            }
        })
        .catch(error => {
            console.log(error);
            toastr.error(error.response.data.message);
        })
}

const btnHandleAttribute = document.getElementById('btn-handle-attribute');
btnHandleAttribute.addEventListener('click', function () {
    if (attributeId) {
        updateAttribute(attributeId);
    } else {
        createAttribute();
    }
});

function saveAttributeValue(textarea, attributeId) {
    // get value of textarea
    const attributeValue = textarea.value;
    console.log(attributeValue);

    // find attribute in product
    const attribute = product.attributes.find(attribute => attribute.attributeId == attributeId);
    attribute.attributeValue = attributeValue;
}

// handle delete attribute
function deleteAttribute(id) {
    const isDelete = confirm("Bạn có chắc chắn muốn xóa thuộc tính này?");
    if (isDelete) {
        axios.delete(`/api/v1/admin/products/${product.productId}/attributes/${id}`)
            .then(response => {
                console.log(response);
                if (response.status === 200) {
                    toastr.success("Xóa thuộc tính thành công");

                    // delete attribute on UI using data-attribute-id
                    const productAttributeList = document.getElementById('product-attribute-list');
                    const attributeItem = productAttributeList.querySelector(`[data-attribute-id="${id}"]`);
                    attributeItem.parentNode.removeChild(attributeItem);

                    // delete attribute in product
                    const attributeIndex = product.attributes.findIndex(attribute => attribute.attributeId == id);
                    product.attributes.splice(attributeIndex, 1);
                }
            })
            .catch(error => {
                console.log(error);
                toastr.error(error.response.data.message);
            })
    }
}
document.addEventListener("DOMContentLoaded", () => {
  const attachment = document.getElementById("attachment");
  const renameMethod = document.getElementById("renameMethod");

  attachment.addEventListener("input", renamedFiles);
  renameMethod.addEventListener("change", renamedFiles);
});

const getExt = (fileName) => {
  return fileName.substring(fileName.lastIndexOf("."));
};

const getFileNameWithoutExt = (fileName) => {
  return fileName.substring(0, fileName.lastIndexOf("."));
};

const renamedFiles = () => {
  const attachment = document.getElementById("attachment").value.trim();
  const method = parseInt(document.getElementById("renameMethod").value);
  const previewSpans = document.querySelectorAll(".preview-name");

  if (!attachment || isNaN(method)) return;

  previewSpans.forEach((span, index) => {
    const originalName = span.getAttribute("data-originalname");
    const baseName = getFileNameWithoutExt(originalName);
    const ext = getExt(originalName);
    let newName = "";

    switch (method) {
      case 0:
        newName = `${attachment}${index + 1}${ext}`;
        break;
      case 1:
        newName = `${baseName}_${attachment}${ext}`;
        break;
      case 2:
        newName = `${attachment}_${baseName}${ext}`;
        break;
      case 3:
        newName = `${baseName}_${attachment}_${index + 1}${ext}`;
        break;
      case 4:
        newName = `${index + 1}_${attachment}_${baseName}${ext}`;
        break;
      case 5:
        const date = new Date().toISOString().split("T")[0];
        newName = `${date}_${attachment}_${baseName}${ext}`;
        break;
      default:
        newName = originalName;
    }

    span.innerText = `→ ${newName}`;
  });
};

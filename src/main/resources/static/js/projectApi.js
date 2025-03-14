const fetchData = async (url, options = {}) => {
  try {
    const res = await fetch(url, options);
    if (!res.ok) {
      const errorData = await res.json();
      throw new Error(errorData.message || `HTTP error! Status: ${res.status}`);
    }
    return await res.json();
  } catch (error) {
    console.error("Fetch error:", error);
    throw error;
  }
};

const fetchAllProjects = async (cb) => {
  try {
    const projects = await fetchData("/projects/");
    cb(projects);
  } catch (error) {
    renderError(error.message);
  }
}

const fetchProjectTypes = async (cb) => {
  try {
    const projectTypes = await fetchData("/projects/types");
    cb(projectTypes);
  } catch (error) {
    renderError(error.message);
  }
}

const addNewProject = async (cb) => {
  const form = document.getElementById("addNewProject").elements;
  const name = form["name"].value;
  const type = form["type"].value;
  const project = {
    name,
    type
  }
  try {
    const newProject = await fetchData("/projects/", { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(project) });
    alert(`New Project added with id ${newProject.id}:(${JSON.stringify(newProject)})`);
    fetchAllProjects(cb);
  } catch (error) {
    renderError(error.message);
  }
}

const renderProjectListCallback = (projectList) => (projects) => {
  projectList.innerHTML = "";
  projects.forEach(project => {
    const projectHtml = document.createElement("p");
    projectHtml.innerHTML = `
            <b>ID :</b>${project.id}</br>
            <b>NAME :</b>${project.name}</br>
            <b>TYPE :</b>${project.type}</br>
        `;
    projectList.appendChild(projectHtml);
  });
}

const renderProjectTypesOptionsCallback = (projectDropdowns) => (projectTypes) => {
  console.log(projectTypes);
  Array.from(projectDropdowns).forEach((dropdown) => {
    dropdown.innerHTML = "";
    projectTypes.forEach(type => {
      const html = document.createElement("option");
      html.setAttribute("value", type);
      html.innerHTML = type;
      dropdown.appendChild(html);
    });
  });
}



const renderError = (message) => {
  alert(`Error calling API: ${message}`);
}

fetchAllProjects(
  renderProjectListCallback(
    document.getElementById("projectList")
  )
);

fetchProjectTypes(
  renderProjectTypesOptionsCallback(
    document.getElementsByClassName("project-type-dropdown")
  )
);
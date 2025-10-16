// Variable global para almacenar el ID del informe que se está editando
let idInformeEnEdicion = null;

// Esperar a que todo el contenido de la página se cargue antes de ejecutar el script
document.addEventListener('DOMContentLoaded', () => {
    // URL base de tu API en Java. Asegúrate de que el puerto sea correcto.
    const API_BASE_URL = '/api';

    // Referencias a los elementos del DOM (la página HTML)
    const dropdownLaboratorio = document.getElementById('idLaboratorioFk');
    const formulario = document.getElementById('formularioInforme');
    const btnBuscar = document.getElementById('btnBuscar');
    const cuerpoTabla = document.getElementById('cuerpoTablaResultados');
    const formTitle = document.querySelector('.seccion h2');

    // --- LÓGICA PARA LOS BOTONES DE AYUDA (FAQ) ---
    const faqLaboratorioBtn = document.getElementById('faq-laboratorio-btn');
    const faqNumSecBtn = document.getElementById('faq-numsec-btn');
    const faqIdOriginalBtn = document.getElementById('faq-idoriginal-btn');
    const faqAnoBtn = document.getElementById('faq-ano-btn');
    const faqNumSolBtn = document.getElementById('faq-numsol-btn'); // Botón para Número de Solicitud

    if (faqLaboratorioBtn) {
        faqLaboratorioBtn.addEventListener('click', () => {
            const mensajeAyuda = `
¿Qué laboratorio seleccionar para un informe antiguo?
Si el informe que estás registrando pertenece a un laboratorio con un nombre anterior, selecciona el laboratorio actual al que corresponde.
Guía de Mapeo:
----------------------------------------------------
• Si el informe es de LABSA, seleccione: LABORATORIO DE GEOTECNIA (LABGEO)
• Si el informe es de LABPM, seleccione: LABORATORIO DE METROLOGIA (LABM)
• Si el informe es de LABQUI, seleccione: LABORATORIO DE ANALISIS INDUSTRIALES... (LABAICA)
• Si el informe es de LABMECU, seleccione: LABORATORIO DE ANALISIS INDUSTRIALES... (LABAICA)
• Si el informe es de LABIAP, seleccione: LABORATORIO DE INVESTIGACION... (LIICA)
----------------------------------------------------
Después, asegúrate de ingresar el identificador original completo (ej. "CEI-LABSA-001-2010") en el campo "ID Informe Original".
            `;
            alert(mensajeAyuda.trim());
        });
    }

    if (faqNumSolBtn) {
        faqNumSolBtn.addEventListener('click', () => {
            alert(
                "Ayuda: Número de Solicitud\n\n" +
                "Ingrese aquí el número o código de la solicitud de trabajo asociada a este informe.\n\n"
            );
        });
    }

    if (faqAnoBtn) {
        faqAnoBtn.addEventListener('click', () => {
            alert(
                "Ayuda: Año del Informe\n\n" +
                "Por favor, ingrese el año que forma parte del identificador oficial del informe (ej. el '2024' en 'CEI-01-S123-001-2024').\n\n" +
                "Este campo NO debe contener el año de la solicitud de trabajo u otras fechas internas del documento."
            );
        });
    }
    
    if (faqNumSecBtn) {
        faqNumSecBtn.addEventListener('click', () => {
            alert(
                "Ayuda: Número Secuencial del Informe\n\n" +
                "Aquí debes ingresar únicamente la parte numérica secuencial que identifica al informe dentro de su año y laboratorio.\n\n" +
                "Por ejemplo, si el identificador en el documento es 'CEI-01-S123-004-2014', aquí deberías ingresar el número 4."
            );
        });
    }

    if (faqIdOriginalBtn) {
        faqIdOriginalBtn.addEventListener('click', () => {
            alert(
                "Ayuda: ID Informe Original\n\n" +
                "Este campo es OPCIONAL y se usa principalmente para informes antiguos o que no siguen el formato estándar.\n\n" +
                "QUÉ PONER:\n" +
                "Ingresa el código de identificación completo tal como aparece en el documento físico. Ejemplo: 'CEI-LABSA-001-2010'.\n\n" +
                "CUÁNDO DEJARLO VACÍO:\n" +
                "Si el informe es reciente y su ID se puede construir con los otros campos, puedes dejar este campo en blanco."
            );
        });
    }
    // --- FIN DE LA LÓGICA DE AYUDA ---


    function cargarLaboratorios() {
        fetch(`${API_BASE_URL}/laboratorios`)
            .then(response => response.json())
            .then(laboratorios => {
                dropdownLaboratorio.innerHTML = '<option value="">Seleccione un laboratorio...</option>';
                laboratorios.forEach(lab => {
                    if (lab.activo) {
                        const option = document.createElement('option');
                        option.value = lab.idLaboratorio;
                        option.textContent = `${lab.nombreActual} (${lab.acronimoActual || 'N/A'})`;
                        dropdownLaboratorio.appendChild(option);
                    }
                });
            }).catch(error => console.error('Error al cargar laboratorios:', error));
    }

    formulario.addEventListener('submit', (evento) => {
        evento.preventDefault();
        
        const idOriginalInput = document.getElementById('idInformeOriginal');
        const idOriginalValue = idOriginalInput.value.trim();
        const formatoRegex = /^CEI-\d{2}-\d+-\d{4}$/;

        if (idOriginalValue !== "" && !formatoRegex.test(idOriginalValue)) {
            alert("El formato del 'ID Informe Original' es incorrecto.\nDebe seguir el patrón: CEI-XX-XXXX-XXXX");
            idOriginalInput.focus();
            return;
        }

        const formData = new FormData(formulario);
        let url = `${API_BASE_URL}/informes`;
        let method = 'POST';

        if (idInformeEnEdicion) {
            url = `${API_BASE_URL}/informes/${idInformeEnEdicion}`;
            method = 'PUT';
        }

        fetch(url, { method: method, body: formData })
            .then(response => {
                if (!response.ok) return response.text().then(text => { throw new Error(text || 'Error en la operación.') });
                return response.json();
            })
            .then(resultado => {
                const accion = idInformeEnEdicion ? 'actualizado' : 'guardado';
                alert(`Informe ${accion} con éxito.\nID del Sistema: ${resultado.idInforme}`);
                limpiarFormulario();
                btnBuscar.click();
            })
            .catch(error => {
                console.error(`Error al ${idInformeEnEdicion ? 'actualizar' : 'guardar'}:`, error);
                alert(`Error al ${idInformeEnEdicion ? 'actualizar' : 'guardar'} el informe: ${error.message}`);
            });
    });

    btnBuscar.addEventListener('click', () => {
        const criterios = new URLSearchParams();
        const camposBusqueda = {
            'idLaboratorioFk': document.getElementById('idLaboratorioFk').value,
            'anioInforme': document.getElementById('anioInforme').value,
            'numeroSolicitud': document.getElementById('numeroSolicitud').value,
            'responsableTecnico': document.getElementById('responsableTecnico').value,
            'empresaCliente': document.getElementById('empresaCliente').value
        };

        for (const [key, value] of Object.entries(camposBusqueda)) {
            if (value && value.trim() !== '') {
                criterios.append(key, value.trim());
            }
        }
        
        fetch(`${API_BASE_URL}/informes?${criterios.toString()}`)
            .then(response => response.json())
            .then(renderizarResultados)
            .catch(error => console.error('Error al buscar:', error));
    });

    function renderizarResultados(resultados) {
        cuerpoTabla.innerHTML = ''; 
        if (!resultados || resultados.length === 0) {
            cuerpoTabla.innerHTML = '<tr><td colspan="6" style="text-align:center;">No se encontraron informes.</td></tr>';
            return;
        }
        resultados.forEach(informe => {
            const linkDocumento = informe.rutaArchivoDigitalizado 
                ? `<a href="${API_BASE_URL}/files/${informe.rutaArchivoDigitalizado}" target="_blank" class="link-documento">Ver Archivo</a>` 
                : 'No adjunto';

            const fila = `
                <tr>
                    <td>${informe.idInformeCEI || 'N/A'}</td>
                    <td>${informe.anioInforme}</td>
                    <td>${informe.descripcionInforme || ''}</td>
                    <td>${informe.responsableTecnico || ''}</td>
                    <td>${linkDocumento}</td>
                    <td><button class="btn-editar" data-id="${informe.idInforme}">Editar</button></td>
                </tr>
            `;
            cuerpoTabla.innerHTML += fila;
        });
    }

    cuerpoTabla.addEventListener('click', (evento) => {
        if (evento.target.classList.contains('btn-editar')) {
            window.editarInforme(evento.target.dataset.id);
        }
    });
    
    function limpiarFormulario() {
        formulario.reset();
        idInformeEnEdicion = null;
        formTitle.textContent = "Gestión de Informes";
        formulario.querySelector('button[type="submit"]').textContent = "Guardar Nuevo Informe";
        document.getElementById('archivo').required = true;
    }

    formulario.addEventListener('reset', limpiarFormulario);
    cargarLaboratorios();
});

window.editarInforme = function(id) {
    console.log(`Cargando informe con ID ${id} para edición...`);
    const API_BASE_URL = 'http://localhost:8080/api';

    fetch(`${API_BASE_URL}/informes/${id}`)
        .then(response => {
            if (!response.ok) throw new Error('No se pudo cargar el informe para editar.');
            return response.json();
        })
        .then(informe => {
            document.getElementById('idLaboratorioFk').value = informe.laboratorio.idLaboratorio;
            document.getElementById('numeroSolicitud').value = informe.numeroSolicitud || '';
            document.getElementById('anioInforme').value = informe.anioInforme;
            document.getElementById('numeroSecuencialInforme').value = informe.numeroSecuencialInforme;
            document.getElementById('tipoSolicitud').value = informe.tipoSolicitud;
            document.getElementById('responsableTecnico').value = informe.responsableTecnico || '';
            document.getElementById('empresaCliente').value = informe.empresaCliente || '';
            document.getElementById('descripcionInforme').value = informe.descripcionInforme || '';
            document.getElementById('idInformeOriginal').value = informe.idInformeOriginal || '';
            document.getElementById('numeroPaginas').value = informe.numeroPaginas || '';
            
            idInformeEnEdicion = informe.idInforme;

            document.querySelector('.seccion h2').textContent = `Editando Informe ID: ${informe.idInforme}`;
            document.querySelector('button[type="submit"]').textContent = "Guardar Cambios";
            document.getElementById('archivo').required = false;

            document.getElementById('formularioInforme').scrollIntoView({ behavior: 'smooth' });
        })
        .catch(error => {
            console.error('Error en editarInforme:', error);
            alert(error.message);
        });
}
// Initialize Vue app
const { createApp, ref, reactive, computed, onMounted } = Vue;

const app = createApp({
    setup() {
        // Navigation
        const currentPage = ref('home');
        const currentStep = ref(1);
        
        // Forms and data
        const userForm = reactive({
            nome: '',
            cpf: '',
            dataNascimento: '',
            email: '',
            senha: '',
            dataIngressoServicoPublico: '',
            cargoAtual: '',
            sexo: '',
            telefone: '',
            orgao: '',
            matricula: ''
        });
        
        const periodoForm = reactive({
            dataInicio: '',
            dataFim: '',
            orgaoEmpregador: '',
            tipoServico: '',
            cargo: '',
            numeroPortaria: '',
            insalubridade: false
        });
        
        // Data collections
        const periodos = ref([]);
        const simulacoes = ref([]);
        
        // UI state
        const showAddPeriodModal = ref(false);
        const selectedRule = ref('REGRA_PERMANENTE');
        
        // Example simulation result
        const simulation = reactive({
            regraAposentadoria: 'REGRA_PERMANENTE',
            tempoContribuicaoAnos: 30,
            tempoContribuicaoMeses: 6,
            tempoContribuicaoDias: 15,
            tempoServicoPublicoAnos: 20,
            tempoServicoPublicoMeses: 3,
            tempoServicoPublicoDias: 10,
            tempoCargoAnos: 10,
            tempoCargoMeses: 2,
            tempoCargoDias: 5,
            dataPrevisaoAposentadoria: '2025-08-15',
            idadeAposentadoria: 58,
            pontuacao: 88,
            percentualBeneficio: 80,
            elegivel: false,
            valorBeneficioEstimado: 4200.00,
            observacoes: 'Para ser elegível nesta regra, você precisa completar mais 7 anos para atingir a idade mínima.'
        });
        
        // Functions
        function formatDate(dateString) {
            if (!dateString) return '';
            const date = new Date(dateString);
            return date.toLocaleDateString('pt-BR');
        }
        
        function formatDateTime(dateTimeString) {
            if (!dateTimeString) return '';
            const date = new Date(dateTimeString);
            return date.toLocaleDateString('pt-BR') + ' ' + date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
        }
        
        function formatTipoServico(tipo) {
            const tipos = {
                'ESTATUTARIO': 'Estatutário',
                'CLT': 'CLT',
                'CRES': 'CRES',
                'SERVICO_PUBLICO_FEDERAL': 'Serviço Público Federal',
                'SERVICO_PUBLICO_ESTADUAL': 'Serviço Público Estadual',
                'SERVICO_PUBLICO_MUNICIPAL': 'Serviço Público Municipal',
                'SERVICO_MILITAR': 'Serviço Militar',
                'INSALUBRE': 'Insalubre',
                'MAGISTERIO': 'Magistério'
            };
            return tipos[tipo] || tipo;
        }
        
        function formatRegraAposentadoria(regra) {
            const regras = {
                'REGRA_PERMANENTE': 'Regra Permanente',
                'REGRA_TRANSICAO_PEDÁGIO': 'Transição - Pedágio',
                'REGRA_TRANSICAO_PONTOS': 'Transição - Pontos',
                'REGRA_ESPECIAL_PROFESSOR': 'Especial - Professor',
                'REGRA_ESPECIAL_POLICIAL': 'Especial - Policial',
                'REGRA_ESPECIAL_INSALUBRIDADE': 'Especial - Insalubridade'
            };
            return regras[regra] || regra;
        }
        
        function formatCurrency(value) {
            return value.toLocaleString('pt-BR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
        }
        
        function calcularTempo(dataInicio, dataFim) {
            if (!dataInicio || !dataFim) return '';
            
            const inicio = new Date(dataInicio);
            const fim = new Date(dataFim);
            
            // Calculate difference in milliseconds
            const diff = fim - inicio;
            
            // Convert to days
            const days = Math.floor(diff / (1000 * 60 * 60 * 24));
            
            // Calculate years, months, and remaining days
            const years = Math.floor(days / 365);
            const months = Math.floor((days % 365) / 30);
            const remainingDays = days % 30;
            
            return `${years} anos, ${months} meses, ${remainingDays} dias`;
        }
        
        function calcularTempoTotal() {
            if (periodos.value.length === 0) return '0 anos, 0 meses, 0 dias';
            
            let totalDays = 0;
            
            periodos.value.forEach(periodo => {
                const inicio = new Date(periodo.dataInicio);
                const fim = new Date(periodo.dataFim);
                const diff = fim - inicio;
                totalDays += Math.floor(diff / (1000 * 60 * 60 * 24));
            });
            
            const years = Math.floor(totalDays / 365);
            const months = Math.floor((totalDays % 365) / 30);
            const days = totalDays % 30;
            
            return `${years} anos, ${months} meses, ${days} dias`;
        }
        
        function adicionarPeriodo() {
            // Validate form
            if (!periodoForm.dataInicio || !periodoForm.dataFim || !periodoForm.orgaoEmpregador || !periodoForm.tipoServico) {
                alert('Preencha os campos obrigatórios!');
                return;
            }
            
            // Add to periodos array
            periodos.value.push({
                dataInicio: periodoForm.dataInicio,
                dataFim: periodoForm.dataFim,
                orgaoEmpregador: periodoForm.orgaoEmpregador,
                tipoServico: periodoForm.tipoServico,
                cargo: periodoForm.cargo,
                numeroPortaria: periodoForm.numeroPortaria,
                insalubridade: periodoForm.insalubridade
            });
            
            // Reset form
            periodoForm.dataInicio = '';
            periodoForm.dataFim = '';
            periodoForm.orgaoEmpregador = '';
            periodoForm.tipoServico = '';
            periodoForm.cargo = '';
            periodoForm.numeroPortaria = '';
            periodoForm.insalubridade = false;
            
            // Close modal
            showAddPeriodModal.value = false;
            
            // Show a modal to confirm the period was added
            // In a real app, we would use a proper modal or toast component
            alert('Período adicionado com sucesso!');
        }
        
        function editPeriodo(index) {
            const periodo = periodos.value[index];
            
            periodoForm.dataInicio = periodo.dataInicio;
            periodoForm.dataFim = periodo.dataFim;
            periodoForm.orgaoEmpregador = periodo.orgaoEmpregador;
            periodoForm.tipoServico = periodo.tipoServico;
            periodoForm.cargo = periodo.cargo;
            periodoForm.numeroPortaria = periodo.numeroPortaria;
            periodoForm.insalubridade = periodo.insalubridade;
            
            // We would remove the old period and add the updated one when the user saves
            periodos.value.splice(index, 1);
            showAddPeriodModal.value = true;
        }
        
        function deletePeriodo(index) {
            if (confirm('Tem certeza que deseja remover este período?')) {
                periodos.value.splice(index, 1);
            }
        }
        
        function executarSimulacao() {
            // In a real app, we would call the backend API
            // For now, we'll just update the simulation object with some example data
            
            simulation.regraAposentadoria = selectedRule.value;
            
            // Determine eligibility based on the rule
            if (selectedRule.value === 'REGRA_PERMANENTE') {
                // Just an example - in a real app this would come from real calculations
                simulation.elegivel = false;
                simulation.observacoes = 'Para ser elegível nesta regra, você precisa completar mais 7 anos para atingir a idade mínima.';
            } else if (selectedRule.value === 'REGRA_TRANSICAO_PEDÁGIO') {
                simulation.elegivel = true;
                simulation.observacoes = 'Você já cumpriu o pedágio necessário e pode se aposentar por esta regra.';
            } else if (selectedRule.value === 'REGRA_ESPECIAL_PROFESSOR') {
                simulation.elegivel = true;
                simulation.observacoes = 'Como professor, você já atingiu os requisitos para aposentadoria especial.';
            } else {
                simulation.elegivel = false;
                simulation.observacoes = 'Você ainda não cumpriu todos os requisitos para esta regra.';
            }
            
            // Move to results step
            currentStep.value = 4;
        }
        
        function salvarSimulacao() {
            // Add the current simulation to the history
            simulacoes.value.unshift({
                ...simulation,
                dataSimulacao: new Date().toISOString(),
                nomeSimulacao: `Simulação ${selectedRule.value} - ${new Date().toLocaleDateString('pt-BR')}`
            });
            
            // Show confirmation and go to home
            alert('Simulação salva com sucesso!');
            currentPage.value = 'history';
        }
        
        function verSimulacao(sim) {
            // Copy the simulation data to the current simulation
            Object.assign(simulation, sim);
            
            // Go to the simulator page and show results
            currentPage.value = 'simulator';
            currentStep.value = 4;
        }
        
        function excluirSimulacao(index) {
            if (confirm('Tem certeza que deseja excluir esta simulação?')) {
                simulacoes.value.splice(index, 1);
            }
        }
        
        // Load example data
        onMounted(() => {
            // Example user
            userForm.nome = 'João Silva';
            userForm.cpf = '123.456.789-00';
            userForm.dataNascimento = '1965-05-15';
            userForm.email = 'joao.silva@exemplo.com';
            userForm.dataIngressoServicoPublico = '1995-02-10';
            userForm.cargoAtual = 'Analista Administrativo';
            userForm.sexo = 'M';
            userForm.orgao = 'Secretaria de Administração';
            userForm.matricula = '12345';
            
            // Example periods
            periodos.value = [
                {
                    dataInicio: '1995-02-10',
                    dataFim: '2005-12-31',
                    orgaoEmpregador: 'Secretaria de Administração',
                    tipoServico: 'ESTATUTARIO',
                    cargo: 'Assistente Administrativo',
                    numeroPortaria: 'PORT-123/1995',
                    insalubridade: false
                },
                {
                    dataInicio: '2006-01-01',
                    dataFim: '2023-08-15',
                    orgaoEmpregador: 'Secretaria de Administração',
                    tipoServico: 'ESTATUTARIO',
                    cargo: 'Analista Administrativo',
                    numeroPortaria: 'PORT-456/2006',
                    insalubridade: false
                },
                {
                    dataInicio: '1990-01-15',
                    dataFim: '1995-01-31',
                    orgaoEmpregador: 'Empresa Privada XYZ',
                    tipoServico: 'CLT',
                    cargo: 'Auxiliar Administrativo',
                    numeroPortaria: 'CTC-789/2010',
                    insalubridade: false
                }
            ];
            
            // Example simulations
            simulacoes.value = [
                {
                    dataSimulacao: '2023-08-10T14:30:00',
                    nomeSimulacao: 'Simulação Regra Permanente',
                    regraAposentadoria: 'REGRA_PERMANENTE',
                    tempoContribuicaoAnos: 33,
                    tempoContribuicaoMeses: 10,
                    tempoContribuicaoDias: 15,
                    tempoServicoPublicoAnos: 28,
                    tempoServicoPublicoMeses: 10,
                    tempoServicoPublicoDias: 21,
                    tempoCargoAnos: 17,
                    tempoCargoMeses: 11,
                    tempoCargoDias: 30,
                    dataPrevisaoAposentadoria: '2025-05-15',
                    idadeAposentadoria: 55,
                    pontuacao: 89,
                    percentualBeneficio: 86.0,
                    elegivel: false,
                    valorBeneficioEstimado: 4500.0,
                    observacoes: 'Faltam alguns anos para atingir idade mínima'
                },
                {
                    dataSimulacao: '2023-08-05T10:15:00',
                    nomeSimulacao: 'Simulação Regra Transição',
                    regraAposentadoria: 'REGRA_TRANSICAO_PEDÁGIO',
                    tempoContribuicaoAnos: 33,
                    tempoContribuicaoMeses: 10,
                    tempoContribuicaoDias: 15,
                    tempoServicoPublicoAnos: 28,
                    tempoServicoPublicoMeses: 10,
                    tempoServicoPublicoDias: 21,
                    tempoCargoAnos: 17,
                    tempoCargoMeses: 11,
                    tempoCargoDias: 30,
                    dataPrevisaoAposentadoria: '2024-02-10',
                    idadeAposentadoria: 55,
                    pontuacao: 89,
                    percentualBeneficio: 100.0,
                    elegivel: true,
                    valorBeneficioEstimado: 5200.0,
                    observacoes: 'Cumpriu os requisitos da regra de transição com pedágio'
                }
            ];
            
            // Initialize Bootstrap components
            // In a real app, this would be handled differently (e.g., using Vue plugins)
            setTimeout(() => {
                const modals = document.querySelectorAll('.modal');
                modals.forEach(modal => {
                    new bootstrap.Modal(modal);
                });
            }, 500);
        });
        
        // Return values and functions for the template
        return {
            currentPage,
            currentStep,
            userForm,
            periodoForm,
            periodos,
            simulacoes,
            showAddPeriodModal,
            selectedRule,
            simulation,
            formatDate,
            formatDateTime,
            formatTipoServico,
            formatRegraAposentadoria,
            formatCurrency,
            calcularTempo,
            calcularTempoTotal,
            adicionarPeriodo,
            editPeriodo,
            deletePeriodo,
            executarSimulacao,
            salvarSimulacao,
            verSimulacao,
            excluirSimulacao
        };
    }
}).mount('#app');

# 💰 Google Maps API - Custos e Preços

## ✅ RESPOSTA RÁPIDA: É GRÁTIS para este app!

**Para o Go2Office**: Você **NUNCA** vai pagar nada! O uso é mínimo e fica dentro do limite gratuito.

---

## 🎁 O QUE O GOOGLE DÁ DE GRAÇA

### Crédito Mensal Grátis:
```
$200 USD por mês = GRÁTIS
```

Este crédito se renova todo mês e **NUNCA expira**.

### Como Funciona:

1. **Google dá** $200 de crédito todo mês
2. **Você usa** a API
3. **Google cobra** do seu crédito grátis
4. Se gastar menos de $200 → **$0 (zero) de custo**
5. Se gastar mais de $200 → Paga apenas o que passar

**Para 99% dos apps pequenos**: Sempre fica dentro dos $200 grátis!

---

## 💵 TABELA DE PREÇOS

### Maps SDK for Android (o que o Go2Office usa):

| Operação | Preço | Incluído Grátis |
|----------|-------|-----------------|
| **Map Load** | $0.007 por carregamento | Até 28.500 carregamentos/mês |
| **Dynamic Maps** | $0.002 por requisição | Até 100.000 requisições/mês |
| **Static Maps** | $0.002 por requisição | Até 100.000 requisições/mês |
| **Street View** | $0.007 por panorama | Até 28.500 panoramas/mês |

### O que o Go2Office usa:
- **Dynamic Maps** (mapa interativo)
- Custo: **$0.002 por uso**
- Grátis até: **100.000 usos por mês**

---

## 🧮 CALCULANDO O CUSTO PARA GO2OFFICE

### Quando o app usa o mapa:

1. **Durante Onboarding** (primeira vez):
   - Usuário clica "Use Map"
   - Mapa carrega: **1 requisição**
   - Usuário confirma localização
   - **Total: 1 requisição = $0.002**

2. **Mudando localização** (raro):
   - Usuário vai em Settings → Auto-Detection
   - Clica "Use Map" novamente
   - **Total: +1 requisição = $0.002**

### Uso típico de um usuário:
```
Primeira configuração: 1 uso
Mudanças (talvez 1-2 vezes no ano): 2 usos
---
Total anual: 3 usos
Custo anual: 3 × $0.002 = $0.006
              ↓
         MENOS DE 1 CENTAVO!
```

### Com o crédito grátis de $200/mês:
```
Seu custo: $0.006 por ano
Crédito grátis: $200 × 12 = $2,400 por ano
---
Você está usando: 0.00025% do seu crédito grátis!
```

---

## 📊 EXEMPLOS REAIS

### Cenário 1: Uso pessoal (1 usuário)
```
Configurações por ano: 2 vezes
Custo: 2 × $0.002 = $0.004
Crédito grátis usado: 0.002%
💰 Custo real: $0.00 (ZERO)
```

### Cenário 2: Pequena empresa (50 usuários)
```
50 usuários × 2 usos/ano = 100 usos
Custo: 100 × $0.002 = $0.20
Crédito grátis usado: 0.1%
💰 Custo real: $0.00 (ZERO)
```

### Cenário 3: Empresa média (1.000 usuários)
```
1.000 usuários × 2 usos/ano = 2.000 usos
Custo: 2.000 × $0.002 = $4.00
Crédito grátis usado: 2%
💰 Custo real: $0.00 (ZERO)
```

### Cenário 4: Grande empresa (10.000 usuários)
```
10.000 usuários × 2 usos/ano = 20.000 usos
Custo: 20.000 × $0.002 = $40.00
Crédito grátis em 1 mês: $200
💰 Custo real: $0.00 (ZERO)
```

---

## 🎯 POR QUE É GRÁTIS PARA ESTE APP?

### 3 Razões:

1. **Uso Único**
   - Usuário configura localização **1 vez**
   - Depois nunca mais usa o mapa
   - Não é um app de navegação (que usa mapa sempre)

2. **Crédito Generoso**
   - $200/mês é MUITO
   - Permite 100.000 requisições/mês
   - O app usa 1-2 requisições por usuário

3. **Não é Streaming**
   - Não carrega mapa continuamente
   - Apenas na configuração inicial
   - GPS e geofencing não usam a API

---

## 💡 COMPARAÇÃO COM OUTROS APPS

### Apps que GASTAM muito:
```
❌ Uber/99: Carrega mapa o tempo todo
❌ Waze: Navegação contínua
❌ iFood: Rastreamento de entregador
❌ Google Maps: Uso constante

Custo: $100-$10.000+ por mês
```

### Go2Office:
```
✅ Carrega mapa 1 vez na configuração
✅ Depois só usa GPS (grátis)
✅ Geofencing é do Android (grátis)
✅ Não usa mapa no dia-a-dia

Custo: $0.00 por mês ✨
```

---

## 🛡️ PROTEÇÕES CONTRA CUSTOS

### O que o Google oferece:

1. **Limites de uso**
   - Você pode definir um limite
   - Exemplo: "Parar em 10.000 requisições/mês"
   - Se atingir o limite, apenas para de funcionar

2. **Alertas**
   - Google envia email quando chega em 50%, 90%, 100%
   - Você pode reagir antes de pagar

3. **Dashboard de monitoramento**
   - Ver uso em tempo real
   - Gráficos e estatísticas
   - Previsão de custo

### Como configurar proteção:

1. Acesse: https://console.cloud.google.com/apis/dashboard
2. Clique no seu projeto
3. Vá em: **"Quotas & System Limits"**
4. Defina um limite (ex: 1.000 requisições/dia)
5. Ative alertas por email

---

## 🔍 OUTROS CUSTOS DA API

### O que NÃO é cobrado:

✅ **GPS do dispositivo** - Grátis (FusedLocationProvider)  
✅ **Geofencing** - Grátis (Android System)  
✅ **Notificações** - Grátis (Android Notifications)  
✅ **Database local** - Grátis (Room/SQLite)  
✅ **Cálculos de horas** - Grátis (código local)  

### O único custo possível:
❌ **Carregar o mapa visual** - $0.002 por vez

---

## 📱 ALTERNATIVAS SEM CUSTO

Se você **NÃO quer usar Google Maps** de jeito nenhum:

### Opção 1: Só GPS + Manual
```kotlin
// Remova o botão "Use Map"
// Mantenha apenas:
- "Use Current" (GPS)
- "Enter Manually"

Custo: $0.00
Funcionalidade: 95% (sem visualização)
```

### Opção 2: OpenStreetMap (grátis)
```kotlin
// Substitua Google Maps por OpenStreetMap
// Biblioteca: osmdroid
// 100% grátis, open source

Custo: $0.00
Funcionalidade: 100%
Desvantagem: UI não tão polida
```

### Opção 3: Mapbox (também tem grátis)
```kotlin
// Mapbox oferece 50.000 carregamentos/mês grátis
// Mais que suficiente

Custo: $0.00 (até 50k)
Funcionalidade: 100%
Alternativa: Similar ao Google Maps
```

---

## 💳 PRECISA DE CARTÃO DE CRÉDITO?

### Sim, MAS:

**Google exige cartão para**:
- Verificar sua identidade
- Prevenir abuso
- Billing em caso de ultrapassar $200

**Mas não cobra nada se**:
- Ficar dentro dos $200/mês
- Configurar limites de uso

### O que acontece:
```
1. Você adiciona cartão
2. Google verifica (pode fazer uma cobrança de $1 e devolver)
3. Você usa a API
4. No fim do mês, Google calcula:
   - Usou: $4.50
   - Crédito grátis: $200
   - Saldo: $200 - $4.50 = $195.50 restante
   - Cobrança no cartão: $0.00 ✅
```

### Para segurança máxima:
```
1. Use um cartão virtual (Nubank, Banco Digital)
2. Defina limite de $1
3. Configure alertas no Google Cloud
4. Defina quota máxima de requisições
```

---

## 📊 MONITORAMENTO DE CUSTOS

### Como ver quanto está gastando:

1. **Acesse o Console**:
   ```
   https://console.cloud.google.com/billing
   ```

2. **Veja o uso atual**:
   - Requisições do mês
   - Custo estimado
   - Crédito restante

3. **Configure alertas**:
   - Alert em 50% ($100)
   - Alert em 90% ($180)
   - Alert em 100% ($200)

### Dashboard mostra:
```
┌─────────────────────────────────────────┐
│ Maps SDK for Android                    │
│                                         │
│ Requisições este mês: 143              │
│ Custo: $0.29                            │
│                                         │
│ Crédito grátis: $200.00                 │
│ Usado: $0.29 (0.14%)                    │
│ Restante: $199.71                       │
│                                         │
│ 💰 Valor a pagar: $0.00                 │
└─────────────────────────────────────────┘
```

---

## ⚠️ SITUAÇÕES QUE PODEM CUSTAR

### Cenários raros que gastariam o crédito:

1. **App viral com milhões de usuários**
   ```
   1.000.000 usuários × 2 usos = 2.000.000 requisições
   Custo: 2.000.000 × $0.002 = $4.000
   Crédito grátis: $200/mês
   Custo real: $3.800 (seria cobrado)
   ```

2. **Bug que recarrega mapa infinitamente**
   ```
   Loop infinito recarregando mapa
   Poderia esgotar rapidamente
   → Por isso use limites de quota!
   ```

3. **Usar APIs adicionais caras**
   ```
   Directions API: $0.005 por requisição
   Geocoding API: $0.005 por requisição
   Places API: $0.017 por requisição
   
   Go2Office NÃO usa essas APIs! ✅
   ```

---

## ✅ RESUMO FINAL

### Para o Go2Office:

| Item | Status |
|------|--------|
| **Custo mensal típico** | $0.00 |
| **Custo anual típico** | $0.00 |
| **Crédito grátis mensal** | $200.00 |
| **Uso estimado** | < 0.1% |
| **Precisa cartão** | Sim (mas não cobra) |
| **Risco de custo** | Praticamente zero |
| **Alternativas grátis** | Sim (GPS + Manual) |

### Conclusão:

🎉 **PODE USAR SEM MEDO!**

- Para 99.99% dos casos: **$0 de custo**
- Crédito grátis é mais que suficiente
- Apenas configuração usa o mapa
- Uso diário não consome API
- Pode configurar proteções

---

## 📞 LINKS ÚTEIS

### Documentação Oficial:
- **Preços**: https://cloud.google.com/maps-platform/pricing
- **Calculadora**: https://cloud.google.com/products/calculator
- **Limites**: https://cloud.google.com/maps-platform/user-guide/usage-limits

### Monitoramento:
- **Console**: https://console.cloud.google.com
- **Billing**: https://console.cloud.google.com/billing
- **Quotas**: https://console.cloud.google.com/apis/dashboard

---

## 💬 PERGUNTAS FREQUENTES

### Q: Vou ter que pagar algo?
**A:** Não! O uso do Go2Office fica completamente dentro do limite grátis de $200/mês.

### Q: Preciso adicionar cartão de crédito?
**A:** Sim, mas é apenas para verificação. Não será cobrado se ficar dentro dos $200/mês.

### Q: E se eu não quiser adicionar cartão?
**A:** Use apenas "Use Current" (GPS) e "Enter Manually". O mapa visual não funcionará, mas o resto do app funciona perfeitamente.

### Q: O que acontece se passar dos $200?
**A:** Muito improvável, mas se passar, você seria cobrado apenas pelo que exceder. Configure limites para prevenir.

### Q: Posso desativar o mapa depois?
**A:** Sim! Basta remover a API key do `local.properties`. O app continua funcionando com GPS e manual.

### Q: Quanto custa por usuário?
**A:** Aproximadamente $0.004 por usuário (menos de meio centavo).

---

**🎊 PODE USAR TRANQUILO - É GRÁTIS PARA ESTE APP! 🎊**

---

*Última atualização: Fevereiro 2026*  
*Preços podem mudar - sempre consulte: https://cloud.google.com/maps-platform/pricing*


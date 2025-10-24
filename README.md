# 📞 ft_hangouts

## 📝 Descrizione del Progetto

[cite_start]**ft_hangouts** è un progetto di sviluppo mobile incentrato sulla creazione di un'applicazione completa per la **gestione dei contatti** e l'invio/ricezione di **messaggi di testo (SMS)**, destinata al sistema operativo Android[cite: 1, 47].

L'obiettivo primario è duplice:
1.  [cite_start]Familiarizzare con le basi dello sviluppo Android[cite: 3].
2.  [cite_start]Acquisire esperienza pratica con il **Ciclo di Vita dell'Activity** e l'interazione con l'Android SDK[cite: 48].

[cite_start]L'applicazione funge da gestore di contatti indipendente, utilizzando il proprio database SQLite per l'archiviazione[cite: 72].

## 🛠️ Stack Tecnologico e Vincoli

| Aspetto | Dettaglio |
| :--- | :--- |
| **Piattaforma** | [cite_start]Android [cite: 5] |
| **Linguaggio** | Kotlin (Scelta consigliata per lo sviluppo nativo moderno) |
| **Persistenza Dati** | [cite_start]Database **SQLite** custom per contatti e messaggi [cite: 72] |
| **SDK** | [cite_start]Android SDK [cite: 49] |
| **IDE Consigliato** | [cite_start]Android Studio [cite: 84] |
| **Vincolo Chiave** | [cite_start]**È vietato l'uso di qualsiasi libreria esterna** (incluso per il design della UI)[cite: 83]. Lo sviluppo deve essere nativo. |

## ✨ Funzionalità Obbligatorie (Mandatory Part)

[cite_start]Il progetto è considerato completo solo se tutte le seguenti funzionalità sono implementate e funzionanti correttamente[cite: 112].

### Gestione Contatti e UI
* [cite_start]Creazione, modifica e cancellazione di un contatto (ogni contatto richiede almeno 5 campi)[cite: 70, 89, 91, 92].
* [cite_start]I contatti sono memorizzati permanentemente nel database SQLite proprietario[cite: 72].
* [cite_start]**Homepage:** Visualizzazione di una lista che riassume ogni contatto[cite: 73, 93].
* [cite_start]**Dettagli:** Possibilità di toccare un contatto per visualizzare i dettagli completi[cite: 74].
* [cite_start]**Temi:** Un menu per cambiare il colore dell'intestazione (header) dell'app[cite: 78, 97].
* [cite_start]**Icona:** L'icona dell'app deve essere il logo di 42[cite: 78, 101].

### Localizzazione e Layout
* [cite_start]Supporto per **due lingue** diverse (una come predefinita)[cite: 75, 98].
* [cite_start]La lingua dell'app si adatta automaticamente al cambio di lingua del sistema operativo[cite: 76].
* [cite_start]L'app deve supportare sia la modalità **landscape** che **portrait**[cite: 100].

### Messaggistica e Activity Lifecycle
* [cite_start]Invio e ricezione di messaggi di testo (SMS) da/verso i contatti salvati[cite: 94, 95].
* [cite_start]Visualizzazione di una cronologia di conversazione chiara, che identifichi mittente e destinatario[cite: 96].
* **Activity Lifecycle Tracking:** Quando l'app va in background, viene salvato un timestamp. [cite_start]Al ritorno in foreground, un **Toast** mostra l'orario dell'ultimo background[cite: 77, 99].

## ⭐ Funzionalità Bonus (Bonus Part)

[cite_start]Le seguenti aggiunte saranno valutate solo se la Parte Obbligatoria è stata completata in modo impeccabile ("Perfect")[cite: 111, 112].

* [cite_start]Aggiungere una foto profilo a ciascun contatto[cite: 106].
* [cite_start]Creare automaticamente un nuovo contatto (con il numero come nome) se si riceve un messaggio da un numero sconosciuto[cite: 107].
* [cite_start]Migliorare l'interfaccia utente aderendo ai principi di **Material Design**[cite: 108].
* [cite_start]Aggiungere la funzionalità per avviare una chiamata al contatto direttamente dall'app[cite: 109].


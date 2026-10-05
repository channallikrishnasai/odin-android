# ODIN — 8-minute finale demo script

**Purpose:** Present ODIN as a serious, local-first Android assistant that connects conversation to useful device actions. This script is designed for a live phone demonstration and a recording. It uses simple, deliberate language and makes clear which capabilities are on-device, which need a network, and which integrations require setup.

> **Before recording:** Walk through this once on the actual phone. Replace each bracketed note with what you really see. Do not claim a feature passed unless you have observed it on the phone. If a step is unreliable, use the fallback line provided and keep the demo moving.

## One-sentence message

> **ODIN brings a local AI assistant, voice interaction, and practical Android tools together on one device, with a path to connect a home when its services are configured.**

## Set up before the camera rolls

| Prepare | Do this |
|---|---|
| Phone | Charge it, set display brightness high, disable notifications that could reveal private content, and keep the screen awake. |
| Language and location | Set the phone/app to English. Confirm Bengaluru appears where the dashboard shows a location. |
| Assistant | Select the local/on-device provider. Download a compatible model over Wi-Fi and confirm it loads by sending a short prompt. |
| Demo prompts | Clear the chat or open a clean conversation. Avoid personal names, messages, contacts, or private documents. |
| OCR page | Print or write this large and clearly: **“Demo appointment — Bengaluru — 14 October, 10:30 AM.”** Use a dark pen or printer on white paper. |
| Timer | Make sure notifications are allowed if you plan to show a timer; use a short timer only if it will not interrupt the remaining script. |
| Offline segment | Load the local model first. Have a plan to switch airplane mode on and back off. Do not use live headlines/weather as proof of offline operation. |
| Smart-home segment | Only use a real light/device if the provider is configured and the device is connected. Otherwise show the provider settings as an integration point and use the fallback wording below. |
| Privacy | Close other apps. Keep API keys, pairing codes, Wi-Fi passwords, and personal account data off-camera. |

**Recommended reliability choice:** If offline chat and OCR are not yet confirmed on the phone, present their screens and explain the design, but label them as “the path we are validating.” Do not stage an airplane-mode success until you have seen it work.

## Eight-minute run of show

| Time | Segment | Screen / action |
|---:|---|---|
| 0:00–0:40 | Opening | ODIN Home screen |
| 0:40–1:25 | The problem and design goal | Keep Home visible |
| 1:25–2:10 | Product at a glance | Point out dashboard and Chat entry |
| 2:10–3:20 | Local assistant | Open Chat; ask one short question |
| 3:20–4:05 | Direct device action | Ask for current time or start a short timer |
| 4:05–5:10 | Camera OCR | Scan the prepared note; inspect extracted text |
| 5:10–6:00 | Voice interaction | Use the microphone for a short English request |
| 6:00–6:50 | Offline and privacy boundary | Show local provider; airplane-mode proof only if verified |
| 6:50–7:30 | Smart-home integration | Show a confirmed device action, or provider setup screen |
| 7:30–8:00 | Close | Return to Home; deliver the takeaway |

## Full spoken script with stage directions

### 0:00–0:40 — Opening

**Show:** ODIN Home screen. Hold still long enough for the audience to read the app name and see the Bengaluru context.

**Say:**

> “Hello. I’m presenting ODIN: a local-first assistant for Android. The idea is straightforward: an assistant should understand a request, use the capabilities of the device, and tell you clearly what it did. I’ll show that flow on the phone, including local chat, a direct action, and text scanning from the camera.”

**Do:** Pause briefly. Do not begin tapping while speaking the opening line.

### 0:40–1:25 — The problem and design goal

**Show:** Keep the Home screen visible. Gesture to the device, not to an audience member’s personal phone.

**Say:**

> “A voice assistant is useful when it answers quickly and can take practical action. A local AI assistant is useful when it can work on the device and keep a request on the device. Many setups make people choose one or build a collection of separate apps and services. ODIN brings those ideas into one Android experience.”
>
> “The design has three parts: a conversation interface, device tools for simple tasks, and provider connections for a home system. They have different requirements, so I’ll show what works locally and call out what needs a connection.”

**Do:** Tap the Chat entry near the end of this section.

### 1:25–2:10 — Product at a glance

**Show:** Home screen, then Chat. Point out the clock/weather/headline area only if it is populated; do not use it as evidence of offline operation.

**Say:**

> “The Home screen is the ambient view. Chat is one tap away. The dashboard can show location, weather, and headlines; those live information feeds need a network. The core interaction is separate: I can talk or type, and choose a local model for on-device assistant responses.”
>
> “For this demo I’m using English and Bengaluru as the location. I’ll start with a short request so you can see the complete interaction without waiting on a long answer.”

**Do:** Open Chat and make sure the message field is visible.

### 2:10–3:20 — Local assistant

**Show:** In Settings or the provider indicator, show that the selected assistant is Local/On-device. Return to Chat. Type or dictate: **“Give me three practical ways to make a small apartment feel calmer in the evening.”**

**Say while the model responds:**

> “This session is set to the on-device provider. The first model download needs internet, but once the model is installed and loaded, this assistant path can run locally. Model speed and answer quality depend on the phone and the model, so I’m using a focused prompt.”

**Say after the answer appears:**

> “Here is the result on the device. I can continue the conversation or ask ODIN to use a tool. The model is the language layer; the Android tools are what connect the assistant to actions.”

**Do:** Scroll only if needed. Keep the answer short and legible. If the reply takes longer than expected, narrate that the phone is loading the local model and give it a few seconds.

**Fallback if local inference fails:**

> “The local model did not finish on this phone in time, so I won’t present this as a successful inference. The provider selection and model setup are visible here; the physical-device check is the remaining validation.”

Then move on. Do not switch to a cloud provider and describe its answer as local.

### 3:20–4:05 — Direct device action

**Show:** In Chat or voice, ask: **“What time is it?”** If the direct timer path is known to be stable, instead say: **“Set a timer for one minute.”**

**Say:**

> “A simple request like the time or a timer does not need a long planning conversation. ODIN has direct intent paths for common actions. The goal is to make everyday requests feel immediate, while reserving model reasoning for requests that need it.”

**Do:** Wait for the response. If using the timer, show the timer confirmation and then dismiss or leave it running as appropriate.

**Fallback if the action does not work:**

> “That action did not confirm on this phone, so I’m treating it as a failed action rather than claiming success. The important product behavior is that an action should be reported as complete only when the device confirms it.”

### 4:05–5:10 — Camera OCR

**Show:** Tap the scan-text/camera action in Chat. Point the camera at the prepared note: “Demo appointment — Bengaluru — 14 October, 10:30 AM.” Capture it and show the extracted text. Ask: **“What date and time are written here?”**

**Say while framing the page:**

> “Now I’m using the camera for a specific task: reading text. ODIN uses on-device Latin text recognition for this scan. Good lighting and a steady, clear page make a difference.”

**Say after the text is extracted:**

> “The extracted text is visible before I rely on it. OCR can make mistakes, so I check the words and then ask a question about the result.”

**Do:** Show the text, then send the question only if the extracted words are correct. Give the audience a moment to read the answer.

**Fallback if capture/OCR fails:**

> “The camera path did not complete on this phone, so I’m not going to imply it did. The feature is designed for on-device text recognition, and this is one of the checks I’m completing on the target device.”

Do not call this object detection, YOLO, or general image understanding. This demo is text recognition.

### 5:10–6:00 — Voice interaction

**Show:** Make sure the mic button is visible. Tap it and say clearly: **“What time is it?”** Wait for the transcript and response.

**Say:**

> “The same assistant can take a spoken request. Speech recognition is a separate part of the pipeline from the language model. For offline English speech, ODIN can use its Vosk path once the model is installed and selected. The microphone permission and the installed speech model both matter.”

**Do:** If the transcript appears, briefly point it out. Avoid testing a long phrase or speaking over the phone speaker.

**Fallback if voice does not work:**

> “Voice input did not complete on this run. I can still use typed chat, and I’m keeping speech recognition as a separate validation item instead of confusing it with whether the assistant model works.”

### 6:00–6:50 — Offline and privacy boundary

**Show:** Show the local provider setting. If offline inference and OCR have already passed on this phone, enable airplane mode, confirm Wi-Fi/cellular are off, then repeat a short local prompt or OCR scan. If not yet validated, keep the phone online and show the local provider setting only.

**Say if the offline check has passed:**

> “I’ve now turned on airplane mode. This short local request [or text scan] still works. That demonstrates the local path after setup. Live weather, news, cloud assistants, and networked smart-home services are not part of this offline claim.”

**Say if it has not yet passed:**

> “The local provider is selected, but I have not completed an airplane-mode run on this phone yet. The first model and speech downloads need a network. I’m separating an offline design goal from an offline result that has actually been measured.”

**Do:** Only say “still works” after seeing the response while airplane mode is active. Restore connectivity after the segment if needed.

### 6:50–7:30 — Smart-home integration

**If a real device is configured:** Show its provider and issue one low-risk command, such as turning a demo light on. Wait for provider confirmation and show the device state.

**Say:**

> “ODIN also has provider connections for home systems such as Home Assistant, MQTT, and SwitchBot. Here I’ve configured [name the provider] and this is the connected [device]. The command was sent and the device confirmed the result.”

**If no device is configured:** Show the provider settings screen without exposing credentials.

**Say:**

> “The app includes provider paths for Home Assistant, MQTT, SwitchBot, and a Matter integration boundary. They need a real endpoint, credentials or pairing, and device testing. I have not configured a live device for this run, so I’m showing the integration point rather than claiming control.”

**Do:** Do not show API tokens, home-service URLs, pairing codes, or private device names.

### 7:30–8:00 — Close

**Show:** Return to the Home screen. Hold the phone steady.

**Say:**

> “ODIN brings local conversation, voice, Android actions, and home integrations into one assistant experience. Today I showed [name only the successful paths: local chat, timer, OCR, voice, offline check, or connected device]. Each capability has a clear setup boundary, and the next step is to validate it on the target phone. The goal is useful intelligence on the device, with visible confirmation when it acts. Thank you.”

## Delivery notes

- Speak at a measured pace; leave a beat after each tap so viewers can see the result.
- Keep the phone near the camera and avoid fast scrolling. Screen recordings should include taps only when useful.
- Use one prompt per feature. Long prompts increase latency and make a live demo harder to follow.
- Say **“local”** only for the local provider path. A local UI does not make a remote model local.
- Say **“offline”** only after a successful run with airplane mode on. Downloads, headlines, weather, cloud providers, and network-connected home services need connectivity.
- Say **“OCR/text recognition”** for the camera segment. Do not call it object detection or general vision.
- Say **“connected and confirmed”** only when a real device responds. A settings page or provider implementation is not a device demonstration.
- If an error appears, name it calmly, state what it means, and move to the next verified feature. A truthful, controlled recovery is more credible than hiding an error.

## One-minute rehearsal checklist

- [ ] Home opens in English and shows Bengaluru where expected.
- [ ] Chat opens and the local model is selected and ready.
- [ ] The short prompt produces a reply within a reasonable wait.
- [ ] The selected direct action responds; the timer will not interrupt the recording.
- [ ] Camera permission is granted and the prepared page is legible in the preview.
- [ ] OCR output is readable and factually correct before sending the follow-up question.
- [ ] Microphone permission and the selected speech model are ready, or the voice fallback is rehearsed.
- [ ] Offline statements match a real airplane-mode check.
- [ ] Smart-home claims match a connected, responsive device—or use the unconfigured-provider script.
- [ ] Notifications, personal data, credentials, and pairing codes are not visible.

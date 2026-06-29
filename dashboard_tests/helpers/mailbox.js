import { ImapFlow } from 'imapflow';
import { simpleParser } from 'mailparser';

export async function waitForResetCode({
  host,
  port,
  secure,
  user,
  password,
  mailbox = 'INBOX',
  subjectIncludes,
  timeoutMs = 60000,
  pollIntervalMs = 5000,
}) {
  const endTime = Date.now() + timeoutMs;

  while (Date.now() < endTime) {
    const client = new ImapFlow({
      host,
      port,
      secure,
      auth: { user, pass: password },
    });

    try {
      await client.connect();
      await client.mailboxOpen(mailbox);

      const messages = [];

      for await (const msg of client.fetch('1:*', {
        uid: true,
        envelope: true,
        source: true,
        flags: true,
      })) {
        messages.push(msg);
      }

      const newestFirst = messages.reverse();

      for (const msg of newestFirst) {
        const subject = msg.envelope?.subject || '';
        const isUnread = !msg.flags?.has('\\Seen');

        if (!isUnread) continue;
        if (subjectIncludes && !subject.includes(subjectIncludes)) continue;

        const parsed = await simpleParser(msg.source);
        const text = parsed.text || parsed.html || '';

        const codeMatch = text.match(/\b(\d{6})\b/);
        if (codeMatch) {
          await client.messageFlagsAdd(msg.uid, ['\\Seen']);
          await client.logout();
          return codeMatch[1];
        }
      }

      await client.logout();
    } catch (error) {
      try {
        await client.logout();
      } catch {}
    }

    await new Promise(resolve => setTimeout(resolve, pollIntervalMs));
  }

  throw new Error('Timed out waiting for password reset email/code');
}
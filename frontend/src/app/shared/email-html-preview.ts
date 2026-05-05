const EMPTY_PREVIEW = '<p style="color:#627184;font-family:Arial,sans-serif;">Le rendu HTML apparaitra ici.</p>';

export function buildEmailPreviewDocument(rawHtml: string | null | undefined): string {
  const html = (rawHtml ?? '').trim() || EMPTY_PREVIEW;
  const parser = new DOMParser();
  const document = parser.parseFromString(html, 'text/html');

  document.querySelectorAll('script, iframe, object, embed, form, input[type="password"]').forEach((element) => element.remove());
  document.querySelectorAll('*').forEach((element) => {
    Array.from(element.attributes).forEach((attribute) => {
      const name = attribute.name.toLowerCase();
      const value = attribute.value.toLowerCase();
      if (name.startsWith('on') || value.includes('javascript:')) {
        element.removeAttribute(attribute.name);
      }
    });
  });

  if (!document.head.querySelector('base')) {
    const base = document.createElement('base');
    base.setAttribute('target', '_blank');
    document.head.prepend(base);
  }

  if (!document.head.querySelector('meta[http-equiv="Content-Security-Policy"]')) {
    const csp = document.createElement('meta');
    csp.setAttribute('http-equiv', 'Content-Security-Policy');
    csp.setAttribute('content', "default-src 'none'; img-src data: https: http:; style-src 'unsafe-inline'; font-src data: https: http:");
    document.head.prepend(csp);
  }

  const style = document.createElement('style');
  style.textContent = 'a{pointer-events:none} body{min-height:100%;}';
  document.head.append(style);

  return '<!doctype html>\n' + document.documentElement.outerHTML;
}

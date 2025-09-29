import React from "react";

function PrivacyPolicy() {
  return (
    <div className="min-h-screen bg-white dark:bg-[var(--black)] text-black dark:text-white p-8 md:p-16">
      <h1 className="text-3xl font-bold mb-6">Privacy Policy</h1>
      <div className="space-y-4 text-lg leading-relaxed">
        <p>
          At LuckyHub, your privacy is important to us. Here’s how we handle
          your data:
        </p>
        <ul className="list-disc pl-6 space-y-2">
          <li>
            <strong>Information We Collect:</strong> Email, username, and usage
            data.
          </li>
          <li>
            <strong>How We Use It:</strong> To provide the LuckyHub service,
            communicate important updates, and improve features.
          </li>
          <li>
            <strong>Sharing:</strong> We do not sell your personal data. We may
            share anonymized data for analytics.
          </li>
          <li>
            <strong>Security:</strong> We implement reasonable measures to
            protect your data.
          </li>
          <li>
            <strong>Cookies & Tracking:</strong> We use cookies to enhance user
            experience.
          </li>
          <li>
            <strong>Your Choices:</strong> You can request account deletion or
            opt out of marketing emails anytime.
          </li>
        </ul>
      </div>
    </div>
  );
}

export default PrivacyPolicy;

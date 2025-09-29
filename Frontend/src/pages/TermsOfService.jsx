import React from "react";

function TermsOfService() {
  return (
    <div className="min-h-screen bg-white dark:bg-[var(--black)] text-black dark:text-white p-8 md:p-16">
      <h1 className="text-3xl font-bold mb-6">Terms of Service</h1>
      <div className="space-y-4 text-lg leading-relaxed">
        <p>
          Welcome to LuckyHub! By using our platform, you agree to the following
          terms:
        </p>
        <ul className="list-disc pl-6 space-y-2">
          <li>
            <strong>Eligibility:</strong> You must be at least 13 years old.
          </li>
          <li>
            <strong>Account Responsibility:</strong> Keep your account details
            confidential. You are responsible for all activity under your
            account.
          </li>
          <li>
            <strong>Content:</strong> You may not post content that is illegal,
            offensive, or violates the rights of others.
          </li>
          <li>
            <strong>Service Use:</strong> LuckyHub is provided “as-is.” We
            reserve the right to modify or discontinue features at any time.
          </li>
          <li>
            <strong>Limitation of Liability:</strong> We are not responsible for
            lost winnings or technical issues.
          </li>
          <li>
            <strong>Termination:</strong> We may suspend or terminate accounts
            that violate these terms.
          </li>
        </ul>
      </div>
    </div>
  );
}

export default TermsOfService;
